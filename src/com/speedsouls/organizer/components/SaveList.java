package com.speedsouls.organizer.components;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.Profile;
import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.content.SortingCategory;
import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.dragndrop.SaveListDragListener;
import com.speedsouls.organizer.dragndrop.SaveListTransferHandler;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;
import com.speedsouls.organizer.listeners.SearchListener;
import com.speedsouls.organizer.listeners.SortingListener;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * SaveList.
 * <p>
 * Displays Save objects in a JList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class SaveList extends JList<Save> implements ListCellRenderer<Save>, ListSelectionListener, ProfileListener, SaveListener,
		SearchListener, SortingListener, MouseListener, KeyListener
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
		fillWith(OrganizerManager.getSelectedProfile());
	}


	/**
	 * Fills the list with the given profile.
	 * 
	 * @param profile the profile to fill this list with
	 */
	private void fillWith(Profile profile)
	{
		File profileDir = profile.getDirectory();
		fillWith(profileDir.listFiles());
	}


	/**
	 * Fills the list with the given files.
	 * 
	 * @param files the files to fill this list with
	 */
	private void fillWith(File[] files)
	{
		if (files == null)
		{
			fillWith(new LinkedList<File>());
			return;
		}
		fillWith(new LinkedList<File>(Arrays.asList(files)));
	}


	/**
	 * Fills the list with the given files.
	 * 
	 * @param files the files to fill this list with
	 */
	private void fillWith(List<File> files)
	{
		if (files != null)
		{
			Save selectedSave = getSelectedValue();
			DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
			model.removeAllElements();
			LinkedList<Save> elements = new LinkedList<>();
			for (File file : files)
				elements.add(new Save(file));
			Collections.sort(elements);
			elements = sortSavesByFolders(elements);
			for (Save save : elements)
				model.addElement(save);
			if (selectedSave != null)
			{
				for (int i = 0; i < model.size(); i++)
				{
					if (model.getElementAt(i).getFile().equals(selectedSave.getFile()))
					{
						setSelectedIndex(i);
						return;
					}
				}
			}
		}
	}


	/**
	 * Sorts the save list by the currently chosen sorting category.
	 */
	public void sort()
	{
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();

		Save selectedSave = getSelectedValue();
		// need to keep track of open directories to close them before sorting and opening them again afterwards
		List<Save> closedDirectories = new LinkedList<>();
		for (int i = model.size() - 1; i >= 0; i--)
		{
			Save save = model.getElementAt(i);
			if (!save.isCollapsed())
				closedDirectories.add(save);
		}
		for (Save save : closedDirectories)
			closeDirectory(save);

		fillWith(OrganizerManager.getSelectedProfile());

		for (int i = closedDirectories.size() - 1; i >= 0; i--)
		{
			for (int j = 0; j < model.size(); j++)
			{
				if (closedDirectories.get(i).getFile().equals(model.getElementAt(j).getFile()))
				{
					File[] files = closedDirectories.get(i).getFile().listFiles();
					if (files != null && files.length > 0)
						openDirectory(model.getElementAt(j));
				}
			}
		}
		if (selectedSave != null)
		{
			for (int i = 0; i < model.size(); i++)
			{
				if (model.getElementAt(i).getFile().equals(selectedSave.getFile()))
				{
					setSelectedIndex(i);
					return;
				}
			}
		}
	}


	/**
	 * Returns the given file array as a list object, sorted by folders first.
	 * 
	 * @param files the files to sort
	 * @return the sorted list
	 */
	private List<File> sortByFolders(File[] files)
	{
		if (files != null)
			return sortByFolders(new LinkedList<File>(Arrays.asList(files)));
		return null;
	}


	/**
	 * Sorts the given file list by folders first.
	 * 
	 * @param files the list to sort
	 * @return the sorted list
	 */
	private List<File> sortByFolders(LinkedList<File> files)
	{
		List<File> folders = new ArrayList<>();
		for (File file : files)
		{
			if (file.isDirectory())
				folders.add(file);
		}
		for (File file : folders)
		{
			files.remove(file);
		}
		List<File> sortedFiles = folders;
		sortedFiles.addAll(files);
		return sortedFiles;
	}


	private LinkedList<Save> sortSavesByFolders(LinkedList<Save> saves)
	{
		LinkedList<Save> folders = new LinkedList<>();
		for (Save save : saves)
		{
			if (save.isDirectory())
				folders.add(save);
		}
		for (Save file : folders)
		{
			saves.remove(file);
		}
		LinkedList<Save> sortedFiles = folders;
		sortedFiles.addAll(saves);
		return sortedFiles;
	}


	/**
	 * Opens the save if it is a directory and inserts its sub-contents below it in the list.
	 * 
	 * @param save the save to open
	 */
	public void openDirectory(Save save)
	{
		if (save == null || !save.isCollapsed())
			return;
		LinkedList<Save> subContents = new LinkedList<>();
		for (File file : save.getFile().listFiles())
			subContents.add(new Save(file));
		if (subContents.size() == 0)
		{
			boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
			JOptionPane.showMessageDialog(getParent(), "Cannot open a folder with no contents!", "Info", JOptionPane.INFORMATION_MESSAGE);
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
			return;
		}
		Collections.sort(subContents);
		subContents = sortSavesByFolders(subContents);
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		int startIndex = model.indexOf(save) + 1;
		for (int i = 0; i < subContents.size(); i++)
		{
			model.add(startIndex + i, subContents.get(i));
		}
		save.setIsCollapsed(false);
	}


	/**
	 * Closes the save if it is a directory and removes its sub-contents displayed in the list.
	 * 
	 * @param save the save to close
	 */
	public void closeDirectory(Save save)
	{
		if (save == null || save.isCollapsed())
			return;
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		File[] subContents = save.getFile().listFiles();
		if (subContents == null)
			return;
		for (File file : subContents)
		{
			for (int i = 0; i < model.size(); i++)
			{
				Save tmp = model.getElementAt(i);
				if (tmp.getFile().equals(file))
				{
					if (tmp.isDirectory())
						closeDirectory(tmp);
					model.removeElementAt(i);
					break;
				}
			}
		}
		save.setIsCollapsed(true);
	}


	/**
	 * Finds the save associated with the given file.
	 * 
	 * @param file the file to try to find the save for
	 * @return the save that is associated with the file. Null if not found
	 */
	public Save getSaveByFile(File file)
	{
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		for (int i = 0; i < model.size(); i++)
		{
			if (model.getElementAt(i).getFile().equals(file))
				return model.getElementAt(i);
		}
		return null;
	}


	/**
	 * Finds any open sub folders of the given save.
	 * 
	 * @param save
	 * @return a list of all open sub folders
	 */
	public List<Save> findOpenSubFolders(Save save)
	{
		List<Save> subFolders = new ArrayList<Save>();
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		for (int i = 0; i < model.size(); i++)
		{
			Save subSave = model.getElementAt(i);
			if (!subSave.isCollapsed())
			{
				if (subSave.isSubContentOf(save))
					subFolders.add(subSave);
			}
		}
		return subFolders;
	}


	/**
	 * Renames the sub-contents of the given save when its being renamed.
	 * 
	 * @param save the save to rename the sub-contents of
	 * @param newName the new name of the save
	 */
	private void renameFolder(Save save, String newName)
	{
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		for (int i = 0; i < model.size(); i++)
		{
			Save tmp = model.getElementAt(i);
			if (tmp.getFile().getParentFile().equals(save.getFile()))
			{
				if (tmp.isDirectory() && !tmp.isCollapsed())
				{
					renameFolder(tmp, tmp.getName());
				}
				tmp.setFile(new File(save.getFile().getParentFile() + File.separator + newName + File.separator + tmp.getName()));
			}

		}
	}


	/**
	 * Asks to create a new folder in this list.
	 */
	public void askToCreateFolder()
	{
		Profile profile = OrganizerManager.getSelectedProfile();
		if (profile == null || profile.getName().equals(""))
			return;
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		String name = JOptionPane.showInputDialog(getParent(), "Folder name: ", "Create Folder", JOptionPane.QUESTION_MESSAGE);
		if (name == null || name.length() < 1)
		{
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
			return;
		}
		name = name.trim();
		try
		{
			OrganizerManager.createFolder(name, profile);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(getParent(), "Error occured when trying to create the folder!", "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	/**
	 * Asks to delete the given list of saves.
	 * 
	 * @param saves the saves to delete
	 */
	public void askToDeleteSaves(List<Save> saves)
	{
		if (saves == null)
			return;
		int confirm = -1;
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		if (saves.size() == 1)
			confirm = JOptionPane.showConfirmDialog(getParent(),
					"Do you really want to delete '" + saves.get(0).getName() + "'"
							+ (saves.get(0).isDirectory() ? " and all of its contents?" : "?"),
					"Delete " + saves.get(0).getName(), JOptionPane.YES_NO_OPTION);
		else if (saves.size() > 1)
			confirm = JOptionPane.showConfirmDialog(getParent(),
					"Do you really want to delete all your selected files and their sub-contents, if any?", "Delete",
					JOptionPane.YES_NO_OPTION);
		if (confirm == 0)
		{
			for (Save save : saves)
			{
				OrganizerManager.removeSave(save, OrganizerManager.getSelectedProfile());
			}
		}
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	/**
	 * Asks to edit the given save.
	 * 
	 * @param save the save to edit
	 */
	public void askToEditSave(Save save)
	{
		if (save == null)
			return;
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		String newName = (String) JOptionPane.showInputDialog(getParent(), (save.isDirectory() ? "Folder name: " : "Save name: "),
				"Edit " + save.getName(), JOptionPane.QUESTION_MESSAGE, null, null, save.getName());
		boolean nameValidation = validateNewName(save, newName);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (nameValidation)
			OrganizerManager.renameSave(save, newName);
	}


	/**
	 * Validates the new name given to a savefile.
	 * 
	 * @param save the savefile
	 * @param newName the new name
	 * @return whether the new name is valid
	 */
	private boolean validateNewName(Save save, String newName)
	{
		if (newName == null)
			return false;
		newName = newName.trim();
		if (newName.length() < 1 || newName.equals(save.getName()))
			return false;
		if (OrganizerManager.containsIllegals(newName))
		{
			JOptionPane.showMessageDialog(getParent(),
					"Illegal characters (~, #, @, *, %, {, }, <, >, [, ], |, “, ”, \\, _, ^) are not allowed!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		// if the name exists and the renaming is not a re-capitalization then don't allow renaming
		File newSaveDir = new File(save.getFile().getParentFile() + File.separator + newName);
		if (newSaveDir.exists() && !save.getName().equalsIgnoreCase(newName))
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


	@Override
	public Component getListCellRendererComponent(JList<? extends Save> list, Save save, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, save, index, isSelected, cellHasFocus);
		label.setText(save.getName());
		label.setBorder(BorderFactory.createEmptyBorder(1, 3 + save.getIndent(), 0, 1));
		if (save.isDirectory())
		{
			label.setFont(getFont().deriveFont(Font.BOLD));
			// label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(save.getFile()));
			if (save.isCollapsed())
				label.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER, 15, new Color(218, 165, 32)));
			else
				label.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER_OPEN, 15, new Color(218, 165, 32)));
		}
		if (save.isReadOnly())
			label.setIcon(new ImageIcon(OrganizerManager.readOnlyIconSmall));

		return label;
	}


	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		OrganizerManager.setSelectedSave(getSelectedValue());
	}


	@Override
	public void profilesUpdated(Game game)
	{
	}


	@Override
	public void changedToProfile(Profile profile)
	{
		fillWith(profile);
	}


	@Override
	public void changedToGame(Game game)
	{
	}


	@Override
	public void addedToProfile(Save save, Profile profile)
	{
		File file = save.getFile();
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		File parentFile = file.getParentFile();
		for (int i = 0; i < model.size(); i++)
		{
			Save parentSave = model.getElementAt(i);
			if (!parentSave.isDirectory())
				continue;
			if (parentSave.getFile().equals(parentFile))
			{
				if (parentSave.isCollapsed())
				{
					openDirectory(parentSave);
					return;
				}
				int index = model.indexOf(parentSave) + parentSave.getFile().listFiles().length;
				model.add(index, save);
				break;
			}
		}
		if (!model.contains(save))
			model.addElement(save);
		sort();
		for (int i = 0; i < model.size(); i++)
		{
			if (model.getElementAt(i).getFile().equals(file))
			{
				setSelectedIndex(i);
				ensureIndexIsVisible(i);
				requestFocusInWindow();
				return;
			}
		}
	}


	@Override
	public void removedFromProfile(Save save, Profile profile)
	{
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		if (!save.isCollapsed())
			closeDirectory(save);
		model.removeElement(save);
	}


	@Override
	public void saveSelected(Save save)
	{
	}


	@Override
	public void saveRenamed(Save save, String newName)
	{
		renameFolder(save, newName);
		save.setFile(new File(save.getFile().getParentFile() + File.separator + newName));
		save.setName(newName);
		File file = save.getFile();
		sort();
		DefaultListModel<Save> model = (DefaultListModel<Save>) getModel();
		for (int i = 0; i < model.size(); i++)
		{
			if (model.getElementAt(i).getFile().equals(file))
			{
				setSelectedIndex(i);
				ensureIndexIsVisible(i);
				return;
			}
		}
	}


	@Override
	public void saveLoadStarted(Save save)
	{
	}


	@Override
	public void saveLoadFinished(Save save)
	{
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
					fillWith(OrganizerManager.getSelectedProfile());
					return;
				}
				File profileDir = OrganizerManager.getSelectedProfile().getDirectory();
				List<File> saves = sortByFolders(profileDir.listFiles());
				List<File> savesToRemove = new ArrayList<>();
				for (File file : saves)
				{
					if (!OrganizerManager.containsFileWithName(file, input))
						savesToRemove.add(file);
				}
				for (File file : savesToRemove)
					saves.remove(file);
				fillWith(saves);
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
				sort();
			}

		});
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() < 2)
			return;
		Save save = getSelectedValue();
		if (save == null || !save.isDirectory())
			return;
		if (save.isCollapsed())
			openDirectory(save);
		else
			closeDirectory(save);
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
	}


	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_F2)
			askToEditSave(getSelectedValue());
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
			askToDeleteSaves(getSelectedValuesList());
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
