package com.soulsspeedruns.organizer.savelist;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.soulsspeedruns.organizer.components.SearchBar;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.games.Profile;
import com.soulsspeedruns.organizer.listeners.NavigationListener;
import com.soulsspeedruns.organizer.listeners.ProfileListener;
import com.soulsspeedruns.organizer.listeners.SaveListener;
import com.soulsspeedruns.organizer.listeners.SearchListener;
import com.soulsspeedruns.organizer.listeners.SortingListener;
import com.soulsspeedruns.organizer.main.config.SortingCategory;
import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SavesManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.messages.AbstractMessage;


/**
 * SaveList.
 * <p>
 * Displays Save objects in a JList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class SaveList extends JList<SaveListEntry> implements ListSelectionListener, ProfileListener, SaveListener, SearchListener, SortingListener,
		MouseListener, KeyListener, NavigationListener
{

	private static final int EMPTY_SPACE_AT_BOTTOM = 50;

	private final TransferHandler transferHandler = new SaveListTransferHandler(this);

	private List<SaveListEntry> copiedEntries = new ArrayList<>();
	private boolean isCut = false;


	/**
	 * Creates a new SaveList.
	 */
	public SaveList()
	{
		super();

		setBorder(BorderFactory.createEmptyBorder(0, 0, EMPTY_SPACE_AT_BOTTOM, 0));

		setCellRenderer(new SaveListRenderer());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		setDragEnabled(true);
		setDropMode(DropMode.INSERT);

		new SaveListDragListener(this);
		setTransferHandler(transferHandler);

		addKeyListener(this);
		addListSelectionListener(this);
		addMouseListener(this);

		GamesManager.addProfileListener(this);
		SavesManager.addSaveListener(this);
		SavesManager.addSearchListener(this);
		SavesManager.addSortingListener(this);
		SavesManager.addNavigationListener(this);

		setModel(new DefaultListModel<>());
		fillWith(GamesManager.getSelectedProfile(), null);
	}


	/**
	 * Returns whether the save list currently stores entries for pasting.
	 * 
	 * @return whether there is copied entries
	 */
	public boolean hasCopiedEntries()
	{
		return !copiedEntries.isEmpty();
	}


	/**
	 * Fills the list with the given profile. If a search term is given, only saves/folders that contain the term will be added.
	 * 
	 * @param profile    the profile to fill this list with
	 * @param searchTerm the search term
	 */
	private void fillWith(Profile profile, String searchTerm)
	{
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		model.removeAllElements();
		addChildrenToList(profile.getRoot(), searchTerm);
	}


	/**
	 * Adds all the children of the given folder and its open subFolders to the list. If a search term is given, only saves/folders that contain the
	 * term will be added.
	 * 
	 * @param folder
	 * @param searchTerm
	 */
	private void addChildrenToList(Folder folder, String searchTerm)
	{
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		if (folder == null)
			return;
		for (SaveListEntry entry : folder.getChildren())
		{
			if (searchTerm == null || (searchTerm.length() > 0 && entry.matchesSearchTerm(searchTerm)))
			{
				model.addElement(entry);
				if (entry instanceof Folder)
				{
					Folder subFolder = (Folder) entry;
					if (!subFolder.isClosed())
						addChildrenToList(subFolder, searchTerm);

				}
			}
		}
	}


	/**
	 * Refreshes all profiles and the savelist to keep it up to date with the filesystem.
	 * 
	 * @param refreshAllProfiles whether to refresh all profiles or just the currently selected one
	 * @param silent             whether to output a message on a successful refresh
	 */
	public void refreshFromFileSystem(boolean silent)
	{
		try
		{
			GamesManager.refreshProfiles();
			fillWith(GamesManager.getSelectedProfile(), null);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(getParent(), "Error occurred when trying to refresh from the file system.", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!silent)
			AbstractMessage.display(AbstractMessage.SUCCESSFUL_REFRESH);
	}


	/**
	 * Refreshes and sorts all entries in the list according to the current sorting method and the current status of Folders and Saves in memory.
	 */
	public void refreshList()
	{
		SaveListEntry selectedEntry = SavesManager.getSelectedEntry();
		Profile currentProfile = GamesManager.getSelectedProfile();
		if (currentProfile.getRoot() == null)
			return;
		currentProfile.getRoot().sort();
		fillWith(currentProfile, null);
		int selectedIndex = ((DefaultListModel<SaveListEntry>) getModel()).indexOf(selectedEntry);
		if (selectedIndex != -1)
		{
			setSelectedIndex(selectedIndex);
			ensureIndexIsVisible(selectedIndex);
		}
	}


	/**
	 * Copies/cuts the currently selected entries.
	 * 
	 * @param isCut whether it is a cut operation
	 */
	public void copyEntries(boolean isCut)
	{
		if (this.isCut && !copiedEntries.isEmpty())
		{
			for (SaveListEntry entry : copiedEntries)
			{
				entry.setMarkedForCut(false);
			}
		}
		copiedEntries = getSelectedValuesList();
		List<SaveListEntry> selectedEntries = getSelectedValuesList();
		for (SaveListEntry entry : getSelectedValuesList())
		{
			if (selectedEntries.contains(entry.getParent()))
				copiedEntries.remove(entry);
			else
				entry.setMarkedForCut(isCut);
		}

		this.isCut = isCut;
		repaint();
	}


	/**
	 * 
	 */
	public void pasteEntries()
	{
		if (copiedEntries.size() <= 0)
			return;

		SaveListEntry selectedEntry = getSelectedValue();
		if (selectedEntry == null)
		{
			Profile selectedProfile = GamesManager.getSelectedProfile();
			if (selectedProfile.getRoot() == null)
				return;
			selectedEntry = selectedProfile.getRoot();
		}

		Folder newParentFolder = getSelectedValue() instanceof Folder ? (Folder) selectedEntry : selectedEntry.getParent();

		for (SaveListEntry entry : copiedEntries)
		{
			if (entry.equals(newParentFolder))
				return;
			if (entry.isParentOf(newParentFolder))
				return;
			if (entry.getParent().equals(newParentFolder) && isCut) // prevent pasting on the same level if it's a cut
				return;
		}

		for (SaveListEntry entry : copiedEntries)
		{
			try
			{
				SavesManager.copyEntry(entry, newParentFolder, false);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		if (isCut)
		{
			deleteEntries(copiedEntries, true);
			copiedEntries = new ArrayList<>();
			isCut = false;
		}

		newParentFolder.setClosed(false);
		refreshList();
	}


	/**
	 * Opens the entry if it is a folder and inserts its children below it in the list.
	 * 
	 * @param the entry to open
	 */
	public void openDirectory(SaveListEntry entry)
	{
		if (entry instanceof Save)
			return;
		Folder folder = (Folder) entry;
		if (!folder.isClosed() || folder.equals(GamesManager.getSelectedProfile().getRoot()))
			return;
		if (entry.getChildren().size() == 0)
		{
			boolean areHotkeysEnabled = SettingsManager.areGlobalHotkeysEnabled();
			SettingsManager.getKeyboardHook().setHotkeysEnabled(false);
			JOptionPane.showMessageDialog(getParent(), "Cannot open an empty folder!", "Info", JOptionPane.INFORMATION_MESSAGE);
			SettingsManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
			return;
		}
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		int startIndex = model.indexOf(entry) + 1;
		for (int i = 0; i < entry.getChildren().size(); i++)
		{
			model.add(startIndex + i, entry.getChildren().get(i));
		}
		((Folder) entry).setClosed(false);
	}


	/**
	 * Closes the entry if it is a folder and removes its children displayed in the list.
	 * 
	 * @param the entry to open
	 */
	public void closeDirectory(SaveListEntry entry)
	{
		if (entry instanceof Save)
			return;
		if (((Folder) entry).isClosed())
			return;
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		for (int i = 0; i < entry.getChildren().size(); i++)
		{
			if (entry.getChildren().get(i).getChildren().size() > 0)
				closeDirectory(entry.getChildren().get(i));
			model.removeElement(entry.getChildren().get(i));
		}
		((Folder) entry).setClosed(true);
	}


	/**
	 * Asks to create a new folder in this list.
	 */
	public void askToCreateFolder()
	{
		boolean areHotkeysEnabled = SettingsManager.areGlobalHotkeysEnabled();
		SettingsManager.getKeyboardHook().setHotkeysEnabled(false);
		String name = JOptionPane.showInputDialog(SwingUtilities.windowForComponent(this), "Folder name: ", "Create Folder",
				JOptionPane.QUESTION_MESSAGE);
		boolean nameValidation = validateNameForNewFolder(name);
		SettingsManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (nameValidation)
		{
			try
			{
				SavesManager.createFolder(name.trim());
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(getParent(), "Error occurred when trying to create the folder!", "Error occurred",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	/**
	 * Validates the potential name for a new folder.
	 * 
	 * @param name the name
	 * @return whether the name is valid
	 */
	private boolean validateNameForNewFolder(String name)
	{
		if (name == null)
			return false;
		name = name.trim();
		if (name.length() < 1)
			return false;
		if (OrganizerManager.containsIllegals(name))
		{
			JOptionPane.showMessageDialog(getParent(), "Illegal characters (" + OrganizerManager.ILLEGAL_CHARACTERS + ") are not allowed!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		SaveListEntry parent = getSelectedValue();
		if (parent == null)
			parent = GamesManager.getSelectedProfile().getRoot();
		if (parent instanceof Save)
			parent = parent.getParent();
		File newSaveDir = new File(parent.getFile() + File.separator + name);
		if (newSaveDir.exists())
		{
			JOptionPane.showMessageDialog(getParent(), "This folder already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}


	/**
	 * Asks to delete the given list of entries.
	 * 
	 * @param entries the entries to delete
	 */
	public void askToDeleteEntries(List<SaveListEntry> entries)
	{
		if (entries == null)
			return;
		int confirm = -1;
		boolean areHotkeysEnabled = SettingsManager.areGlobalHotkeysEnabled();
		SettingsManager.getKeyboardHook().setHotkeysEnabled(false);
		if (entries.size() == 1)
			confirm = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(this),
					"Do you really want to delete '" + entries.get(0).getName() + "'"
							+ (entries.get(0) instanceof Folder ? " and all of its contents?" : "?"),
					"Delete " + entries.get(0).getName(), JOptionPane.YES_NO_OPTION);
		else if (entries.size() > 1)
			confirm = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(this),
					"Do you really want to delete all your selected files and their sub-contents, if any?", "Delete Files",
					JOptionPane.YES_NO_OPTION);
		if (confirm == 0)
		{
			for (SaveListEntry saveListEntry : entries)
			{
				// if the parent file cannot be written to, then deletion cannot happen
				if (!saveListEntry.getFile().getParentFile().canWrite())
				{
					JOptionPane.showMessageDialog(getParent(), "Couldn't delete files. They are probably being accessed by another program.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			deleteEntries(entries, false);
		}
		SettingsManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	/**
	 * Deletes the given entries from the list and their associated files.
	 * 
	 * @param entries the entries to delete
	 */
	private void deleteEntries(List<SaveListEntry> entries, boolean silent)
	{
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		for (SaveListEntry entry : entries)
		{
			closeDirectory(entry);
			model.removeElement(entry);
			entry.delete();
		}
		if (!silent)
			AbstractMessage.display(AbstractMessage.SUCCESSFUL_DELETE);
	}


	/**
	 * Asks to edit the given entry.
	 * 
	 * @param entry the entry to edit
	 */
	public void askToEditEntry(SaveListEntry entry)
	{
		if (entry == null)
			return;
		if (!entry.canBeRenamed())
		{
			JOptionPane.showMessageDialog(getParent(),
					"This entry can currently not be renamed. It is probably being accessed by another program or in read-only.", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		boolean areHotkeysEnabled = SettingsManager.areGlobalHotkeysEnabled();
		SettingsManager.getKeyboardHook().setHotkeysEnabled(false);
		String newName = (String) JOptionPane.showInputDialog(SwingUtilities.windowForComponent(this),
				(entry instanceof Folder ? "Folder name: " : "Save name: "), "Edit " + entry.getName(), JOptionPane.QUESTION_MESSAGE, null, null,
				entry.getName());
		boolean nameValidation = validateNewName(entry, newName);
		SettingsManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (!nameValidation)
			return;
		renameEntry(entry, newName);
	}


	/**
	 * Renames the entry with the given name and sorts the list afterwards.
	 * 
	 * @param entry   the entry to rename
	 * @param newName the new name
	 */
	private void renameEntry(SaveListEntry entry, String newName)
	{
		if (entry.rename(newName))
			SavesManager.fireEntryRenamedEvent(entry);
	}


	/**
	 * Validates the new name given to an entry.
	 * 
	 * @param entry   the savefile
	 * @param newName the new name
	 * @return whether the new name is valid
	 */
	private boolean validateNewName(SaveListEntry entry, String newName)
	{
		if (newName == null)
			return false;
		newName = newName.trim();
		if (newName.length() < 1 || newName.equals(entry.getName()))
			return false;
		if (OrganizerManager.containsIllegals(newName))
		{
			JOptionPane.showMessageDialog(getParent(), "Illegal characters (" + OrganizerManager.ILLEGAL_CHARACTERS + ") are not allowed!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		// if the name exists and the renaming is not a re-capitalization then don't allow renaming
		File newSaveDir = new File(entry.getFile().getParentFile() + File.separator + newName);
		if (newSaveDir.exists() && !entry.getName().equalsIgnoreCase(newName))
		{
			JOptionPane.showMessageDialog(getParent(), "This name already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}


	/**
	 * Own selection implementation due to the Darklaf implementation throwing an exception without swingx library. This function also allows removing
	 * any selection by clicking on empty space within the list.
	 * 
	 * @param event the click event
	 */
	private void handleSelection(MouseEvent event)
	{
		int index = locationToIndex(event.getPoint());
		if (index < 0 || !getCellBounds(index, index).contains(event.getPoint()))
		{
			clearSelection();
			return;
		}
		if (!event.isShiftDown())
		{
			if (!event.isControlDown())
			{
				if (SwingUtilities.isRightMouseButton(event))
					return;
				clearSelection();
				addSelectionInterval(index, index);
				return;
			}
			if (isSelectedIndex(index))
			{
				removeSelectionInterval(index, index);
				return;
			}
			addSelectionInterval(index, index);
			return;
		}

		int anchorIndex = getAnchorSelectionIndex();
		if (anchorIndex == -1)
		{
			addSelectionInterval(index, index);
			return;
		}
		clearSelection();
		addSelectionInterval(anchorIndex, index);
	}


	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		SavesManager.setSelectedEntry(getSelectedValue());
	}


	@Override
	public void profileDeleted(Profile profile)
	{
	}


	@Override
	public void profileCreated(Profile profile)
	{
	}


	@Override
	public void changedToProfile(Profile profile)
	{
		if (profile.getRoot() != null)
			profile.getRoot().sort();
		fillWith(profile, null);
		for (SaveListEntry entry : copiedEntries)
		{
			entry.setMarkedForCut(false);
		}
		copiedEntries = new ArrayList<>();
		isCut = false;
	}


	@Override
	public void profileDirectoryChanged(Game game)
	{
	}


	@Override
	public void changedToGame(Game game)
	{
	}


	@Override
	public void entryCreated(SaveListEntry entry)
	{
		entry.getParent().setClosed(false);
		refreshList();
		int newIndex = ((DefaultListModel<SaveListEntry>) getModel()).indexOf(entry);
		setSelectedIndex(newIndex);
		ensureIndexIsVisible(newIndex);
		requestFocusInWindow();
	}


	@Override
	public void entryRenamed(SaveListEntry entry)
	{
		refreshList();
	}


	@Override
	public void entrySelected(SaveListEntry entry)
	{
	}


	@Override
	public void saveLoadStarted(Save save)
	{
	}


	@Override
	public void saveLoadFinished(Save save)
	{
		int newIndex = ((DefaultListModel<SaveListEntry>) getModel()).indexOf(save);
		setSelectedIndex(newIndex);
		requestFocusInWindow();
	}


	@Override
	public void gameFileWritableStateChanged(boolean writeable)
	{
	}


	@Override
	public void searchRequested(String input)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (input == null || input.equals("") || input.equals(SearchBar.DEFAULT_TEXT))
				{
					fillWith(GamesManager.getSelectedProfile(), null);
					return;
				}
				fillWith(GamesManager.getSelectedProfile(), input);
			}
		});
	}


	@Override
	public void sortingChanged(SortingCategory category)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				refreshList();
			}

		});
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
		AbstractMessage.clearAllMessages();
		if (e.getClickCount() < 2)
			return;
		SaveListEntry entry = getSelectedValue();
		if (entry instanceof Folder)
		{
			if (((Folder) entry).isClosed())
				openDirectory(entry);
			else
				closeDirectory(entry);
		}
		else if (entry instanceof Save)
		{
			if (SettingsManager.isDoubleClickLoadEnabled())
				SavesManager.loadSave((Save) entry);
		}

	}


	@Override
	public void mouseEntered(MouseEvent e)
	{
	}


	@Override
	public void mouseExited(MouseEvent e)
	{
	}


	@Override
	public void mousePressed(MouseEvent e)
	{
		e.consume();
		requestFocusInWindow();
		handleSelection(e);
	}


	@Override
	public void mouseReleased(MouseEvent e)
	{
		e.consume();
		if (e.isPopupTrigger())
			new SaveListContextMenu(this, e.getX(), e.getY());
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_C && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)
			copyEntries(false);
		else if (e.getKeyCode() == KeyEvent.VK_X && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)
			copyEntries(true);
		else if (e.getKeyCode() == KeyEvent.VK_V && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)
			pasteEntries();

	}


	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_F2)
			askToEditEntry(getSelectedValue());
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
			askToDeleteEntries(getSelectedValuesList());
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
	}


	@Override
	public void navigatedToPrevious()
	{
		setSelectedIndex(Math.max(0, getSelectedIndex() - 1));
	}


	@Override
	public void navigatedToNext()
	{
		setSelectedIndex(Math.min(getModel().getSize(), getSelectedIndex() + 1));
	}

}
