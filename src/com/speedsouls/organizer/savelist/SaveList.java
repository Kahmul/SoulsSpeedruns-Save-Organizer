package com.speedsouls.organizer.savelist;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.dragndrop.SaveListDragListener;
import com.speedsouls.organizer.dragndrop.SaveListTransferHandler;
import com.speedsouls.organizer.games.Game;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;
import com.speedsouls.organizer.listeners.SearchListener;
import com.speedsouls.organizer.listeners.SortingListener;
import com.speedsouls.organizer.messages.AbstractMessage;
import com.speedsouls.organizer.profileconfig.Profile;


/**
 * SaveList.
 * <p>
 * Displays Save objects in a JList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class SaveList extends JList<SaveListEntry> implements ListCellRenderer<SaveListEntry>, ListSelectionListener, ProfileListener,
		SaveListener, SearchListener, SortingListener, MouseListener, KeyListener
{

	private static final long serialVersionUID = 4832551527054891457L;

	private static final int EMPTY_SPACE_AT_BOTTOM = 50;


	/**
	 * Creates a new SaveList.
	 */
	public SaveList()
	{
		super();

		setBorder(BorderFactory.createEmptyBorder(0, 0, EMPTY_SPACE_AT_BOTTOM, 0));

		setCellRenderer(this);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		setDragEnabled(true);
		setDropMode(DropMode.INSERT);

		new SaveListDragListener(this);
		setTransferHandler(new SaveListTransferHandler(this));

		addKeyListener(this);
		addListSelectionListener(this);
		addMouseListener(this);

		OrganizerManager.addProfileListener(this);
		OrganizerManager.addSaveListener(this);
		OrganizerManager.addSearchListener(this);
		OrganizerManager.addSortingListener(this);

		setModel(new DefaultListModel<>());
		fillWith(OrganizerManager.getSelectedProfile(), null);
	}


	/**
	 * Fills the list with the given profile. If a search term is given, only saves/folders that contain the term will be added.
	 *
	 * @param profile the profile to fill this list with
	 * @param searchTerm the search term
	 */
	private void fillWith(Profile profile, String searchTerm)
	{
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		model.removeAllElements();
		addChildrenToList(profile.getRoot(), searchTerm);
	}


	/**
	 * Adds all the children of the given folder and its open subFolders to the list. If a search term is given, only
	 * saves/folders that contain the term will be added.
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
			if (searchTerm != null && searchTerm.length() > 0)
			{
				if (entry.matchesSearchTerm(searchTerm))
					model.addElement(entry);
				continue;
			}
			model.addElement(entry);
			if (entry instanceof Folder)
			{
				Folder subFolder = (Folder) entry;
				if (!subFolder.isClosed())
					addChildrenToList(subFolder, searchTerm);
			}
		}
	}


	/**
	 * Refreshes all profiles and the savelist to keep it up to date with the filesystem.
	 */
	public void refresh()
	{
		try
		{
			internalRefresh();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(getParent(), "Error occurred when trying to refresh from the file system.", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		AbstractMessage.display(AbstractMessage.SUCCESSFUL_REFRESH);
	}

	public void silentRefresh(){
		try
		{
			internalRefresh();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(getParent(), "Error occurred when trying to refresh from the file system.", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void internalRefresh(){
		OrganizerManager.refreshProfiles();
		fillWith(OrganizerManager.getSelectedProfile(), null);
	}


	/**
	 * Sorts all entries in the list according to the current sorting method.
	 */
	public void sortEntries()
	{
		SaveListEntry selectedEntry = OrganizerManager.getSelectedEntry();
		Profile currentProfile = OrganizerManager.getSelectedProfile();
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
	 * Opens the entry if it is a folder and inserts its children below it in the list.
	 * 
	 * @param entry the save to open
	 */
	public void openDirectory(SaveListEntry entry)
	{
		if (entry instanceof Save)
			return;
		Folder folder = (Folder) entry;
		if (!folder.isClosed() || folder.equals(OrganizerManager.getSelectedProfile().getRoot()))
			return;
		if (entry.getChildren().size() == 0)
		{
			boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
			JOptionPane.showMessageDialog(getParent(), "Cannot open a folder with no contents!", "Info", JOptionPane.INFORMATION_MESSAGE);
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
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
	 * @param entry the save to close
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
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		String name = JOptionPane.showInputDialog(getParent(), "Folder name: ", "Create Folder", JOptionPane.QUESTION_MESSAGE);
		boolean nameValidation = validateNameForNewFolder(name);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (nameValidation)
		{
			try
			{
				OrganizerManager.createFolder(name.trim());
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(getParent(), "Error occured when trying to create the folder!", "Error occured",
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
			JOptionPane.showMessageDialog(getParent(), "Illegal characters (" + OrganizerManager.ILLEGAL_CHARACTERS + ") are not allowed!",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		SaveListEntry parent = getSelectedValue();
		if (parent == null)
			parent = OrganizerManager.getSelectedProfile().getRoot();
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
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		if (entries.size() == 1)
			confirm = JOptionPane.showConfirmDialog(getParent(),
					"Do you really want to delete '" + entries.get(0).getName() + "'"
							+ (entries.get(0) instanceof Folder ? " and all of its contents?" : "?"),
					"Delete " + entries.get(0).getName(), JOptionPane.YES_NO_OPTION);
		else if (entries.size() > 1)
			confirm = JOptionPane.showConfirmDialog(getParent(),
					"Do you really want to delete all your selected files and their sub-contents, if any?", "Delete",
					JOptionPane.YES_NO_OPTION);
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
		if (confirm == 0)
			deleteEntries(entries);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	/**
	 * Deletes the given entries from the list and their associated files.
	 * 
	 * @param entries the entries to delete
	 */
	private void deleteEntries(List<SaveListEntry> entries)
	{
		DefaultListModel<SaveListEntry> model = (DefaultListModel<SaveListEntry>) getModel();
		for (SaveListEntry entry : entries)
		{
			closeDirectory(entry);
			model.removeElement(entry);
			entry.delete();
		}
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
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		String newName = (String) JOptionPane.showInputDialog(getParent(), (entry instanceof Folder ? "Folder name: " : "Save name: "),
				"Edit " + entry.getName(), JOptionPane.QUESTION_MESSAGE, null, null, entry.getName());
		boolean nameValidation = validateNewName(entry, newName);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (!entry.canBeRenamed())
		{
			JOptionPane.showMessageDialog(getParent(),
					"This entry can currently not be renamed. It is probably being accessed by another program.", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (nameValidation)
			renameEntry(entry, newName);
	}


	/**
	 * Renames the entry with the given name and sorts the list afterwards.
	 * 
	 * @param entry the entry to rename
	 * @param newName the new name
	 */
	private void renameEntry(SaveListEntry entry, String newName)
	{
		if (entry.rename(newName))
			OrganizerManager.fireEntryRenamedEvent(entry);
	}


	/**
	 * Validates the new name given to an entry.
	 * 
	 * @param entry the savefile
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
			JOptionPane.showMessageDialog(getParent(), "Illegal characters (" + OrganizerManager.ILLEGAL_CHARACTERS + ") are not allowed!",
					"Warning", JOptionPane.WARNING_MESSAGE);
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
	 * Checks whether this MouseEvent should bring up the context menu.
	 * 
	 * @param event the event to check
	 */
	private void checkForPopUp(MouseEvent event)
	{
		int index = locationToIndex(new Point(event.getX(), event.getY()));
		if (index == -1 || !getCellBounds(index, index).contains(new Point(event.getX(), event.getY())))
			clearSelection();
		if (event.isPopupTrigger())
			new SaveListContextMenu(this, event.getX(), event.getY());
	}

	public void copyFiles(){

		try {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			SaveListFileTransferable saveListFileTransferable = new SaveListFileTransferable(this);
			if(!saveListFileTransferable.arrayList.isEmpty()) {
				c.setContents(saveListFileTransferable, SaveListFileTransferable.clipboardOwner);
				AbstractMessage.display(AbstractMessage.SUCCESSFUL_COPY);
			}

		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Error when trying to copy items!", "Error occurred", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void askToPasteFiles() {
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		Folder dirToOpen;
		try {
			dirToOpen = getSelectedValue() instanceof Folder ? (Folder) getSelectedValue() : OrganizerManager.getSelectedProfile().getRoot();
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable transferable = c.getContents(null);

			ArrayList<File> fileList = (ArrayList<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
			if (!fileList.isEmpty() && wouldNotCauseInfiniteLoop(dirToOpen,fileList)) {
				int confirm = JOptionPane.showConfirmDialog(getParent(),
						"Paste " + fileList.size() + " file(s) into '"
								+ dirToOpen.getName() + "' ?", "Paste Items", JOptionPane.YES_NO_OPTION);
				if (confirm == 0) {
					for (File file : fileList) {
						OrganizerManager.copyFile(file, dirToOpen);

					}
					AbstractMessage.display(AbstractMessage.SUCCESSFUL_PASTE);
					silentRefresh();
					setSelectedValue(dirToOpen,true);
					if(dirToOpen != OrganizerManager.getSelectedProfile().getRoot()){
						openDirectory(dirToOpen); // not sure why this doesn't re-open the directory
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error when trying to paste items!", "Error occurred", JOptionPane.ERROR_MESSAGE);
		}
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	private boolean wouldNotCauseInfiniteLoop(Folder dirToOpen, ArrayList<File> fileList) {
		for(File file: fileList){
			if(isSourceAParentOfDest(dirToOpen,file)){
				JOptionPane.showMessageDialog(null, "Cannot paste files into themselves! Please select a different folder to paste your files", "Error occurred", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * This method is used to be sure that the source is not a child of the destination.
	 * This would cause an infinite loop with the current {@link OrganizerManager#copyDirectory(File, File)}
	 * because the src would always have new files as they were copied over so it would never break out the loop
	 * @param dest file trying to paste to
	 * @param src file being pasted from
	 * @return if source is a parent of the destination
	 */
	private boolean isSourceAParentOfDest(Folder dest, File src) {
		boolean answer;
		if (dest.getParent() != null) {
			if (src.equals(dest.getFile())) {
				return true;
			}
			answer = isSourceAParentOfDest(dest.getParent(),src);
			return answer;
		}
		return false;

	}


	@Override
	public Component getListCellRendererComponent(JList<? extends SaveListEntry> list, SaveListEntry entry, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, entry, index, isSelected, cellHasFocus);
		entry.render(label);
		return label;
	}


	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		OrganizerManager.setSelectedEntry(getSelectedValue());
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
		OrganizerManager.refreshProfiles();
		if (profile.getRoot() != null)
			profile.getRoot().sort();
		fillWith(profile, null);
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
		sortEntries();
		int newIndex = ((DefaultListModel<SaveListEntry>) getModel()).indexOf(entry);
		setSelectedIndex(newIndex);
		requestFocusInWindow();
	}


	@Override
	public void entryRenamed(SaveListEntry entry)
	{
		sortEntries();
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
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				if (input == null || input.equals("") || input.equals(SearchBar.DEFAULT_TEXT))
				{
					fillWith(OrganizerManager.getSelectedProfile(), null);
					return;
				}
				fillWith(OrganizerManager.getSelectedProfile(), input);
			}
		});
	}


	@Override
	public void sortingChanged(SortingCategory category)
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				sortEntries();
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
			if (((Folder) entry).isClosed())
				openDirectory(entry);
			else
				closeDirectory(entry);
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
		checkForPopUp(e);
	}


	@Override
	public void mouseReleased(MouseEvent e)
	{
		checkForPopUp(e);
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_C && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK){
			copyFiles();
		}
		else if(e.getKeyCode() == KeyEvent.VK_V && e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK){
			askToPasteFiles();
		}
	}


	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_F2)
			askToEditEntry(getSelectedValue());
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
			askToDeleteEntries(getSelectedValuesList());
		else if(e.getKeyCode() == KeyEvent.VK_COPY){
			JOptionPane.showMessageDialog(this, "This folder already exists!", "Error occured", JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	private static class SaveListFileTransferable implements Transferable {

		protected static final ClipboardOwner clipboardOwner = (clipboard, contents) -> {

		};

		private final ArrayList<File> arrayList = new ArrayList<>();

		private SaveListFileTransferable(SaveList saveList) {
			for(SaveListEntry saveListEntry :saveList.getSelectedValuesList()){
				arrayList.add(saveListEntry.getFile());
			}
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{DataFlavor.javaFileListFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.javaFileListFlavor.equals(flavor);
		}

		@Override
		public ArrayList<File> getTransferData(DataFlavor flavor) {
			return arrayList;
		}
	}

}
