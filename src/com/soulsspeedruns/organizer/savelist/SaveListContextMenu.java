package com.soulsspeedruns.organizer.savelist;


import java.awt.Color;
import java.awt.Desktop;
import java.awt.Point;
import java.io.File;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.soulsspeedruns.organizer.main.config.SortingCategory;
import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;

import jiconfont.icons.Elusive;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * Save List Context Menu.
 * <p>
 * Context menu for the SaveList class. Offers option to add/edit/delete saves.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 1 Oct 2015
 */
public class SaveListContextMenu extends JPopupMenu
{

	private SaveList saveList;
	private Point p;

	private JMenuItem itemAdd;
	private JMenuItem itemRemove;
	private JMenuItem itemEdit;
	private JMenuItem itemCopy;
	private JMenuItem itemCut;
	private JMenuItem itemPaste;
	private JMenuItem itemReadOnly;
	private JMenuItem itemRefresh;
	private JMenuItem itemOpenInExplorer;

	private boolean isSelectionWritable;


	/**
	 * Creates and shows a new context menu for the given SaveList.
	 * 
	 * @param saveList the savelist to create this menu for
	 * @param x        the x coordinate to create the menu at
	 * @param y        the y coordinate to create the menu at
	 */
	public SaveListContextMenu(SaveList saveList, int x, int y)
	{
		super();

		this.saveList = saveList;
		p = new Point(x, y);

		initMenuItems();
		show(saveList, x, y);

		initMenuItemStates();
	}


	/**
	 * Adds all the menu items to the context menu.
	 */
	private void initMenuItems()
	{
		itemAdd = createAddItem(saveList);
		itemRemove = createRemoveItem(saveList);
		itemEdit = createEditItem(saveList);
		itemCopy = createCopyItem(saveList);
		itemCut = createCutItem(saveList);
		itemPaste = createPasteItem(saveList);
		itemReadOnly = createReadOnlyItem(saveList);
		itemRefresh = createRefreshItem(saveList);
		itemOpenInExplorer = createOpenInExplorerItem(saveList);

		add(itemAdd);
		add(new JSeparator());
		add(itemRemove);
		add(itemEdit);
		add(itemCopy);
		add(itemCut);
		add(itemPaste);
		if (GamesManager.getSelectedGame().supportsReadOnly())
			add(itemReadOnly);
		add(new JSeparator());
		add(itemRefresh);
		add(itemOpenInExplorer);
	}


	/**
	 * Handles enabling/disabling the menu items according to the current environment and list selection.
	 */
	private void initMenuItemStates()
	{
		itemReadOnly.setEnabled(false);
		itemPaste.setEnabled(saveList.hasCopiedEntries());
		if (!GamesManager.isAProfileSelected())
		{
			itemAdd.setEnabled(false);
			itemRefresh.setEnabled(false);
			itemOpenInExplorer.setEnabled(false);
			itemPaste.setEnabled(false);
		}
		int index = saveList.locationToIndex(p);
		if (index != -1 && saveList.getCellBounds(index, index).contains(p))
		{
			List<SaveListEntry> selectedEntries = saveList.getSelectedValuesList();

			// select the entry that was right clicked if multiple others aren't selected already
			if (selectedEntries.size() <= 1 || !selectedEntries.contains(saveList.getModel().getElementAt(index)))
			{
				saveList.setSelectedIndex(index);
				selectedEntries = saveList.getSelectedValuesList();
			}

			itemEdit.setEnabled(true);
			itemRemove.setEnabled(true);

			initReadOnlyItemState(selectedEntries);
			return;
		}
		itemEdit.setEnabled(false);
		itemCopy.setEnabled(false);
		itemCut.setEnabled(false);
		itemRemove.setEnabled(false);
	}


	/**
	 * Handles the readonly item state.
	 * 
	 * @param selectedEntries the entries selected in the savelist
	 */
	private void initReadOnlyItemState(List<SaveListEntry> selectedEntries)
	{
		for (SaveListEntry entry : selectedEntries)
		{
			if (entry instanceof Save)
			{
				itemReadOnly.setEnabled(true);
				if (entry.getFile().canWrite())
				{
					isSelectionWritable = true;
					return;
				}
			}
		}

		if(itemReadOnly.isEnabled())
		{
			itemReadOnly.setIcon(IconsAndFontsManager.getWritableIcon(IconsAndFontsManager.ICON_SIZE_MEDIUM, false));
			itemReadOnly.setText("Disable 'Read-Only'");
		}
	}


	/**
	 * Creates the 'Add Folder' item of the context menu.
	 * 
	 * @param saveList
	 * @return the add folder item
	 */
	private JMenuItem createAddItem(SaveList saveList)
	{
		JMenuItem itemAdd = new JMenuItem("Add Folder");
		itemAdd.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER, 16, new Color(251, 208, 108)));
		itemAdd.addActionListener(event -> {
			saveList.askToCreateFolder();
		});
		return itemAdd;
	}


	/**
	 * Creates the 'Remove' item of the context menu.
	 * 
	 * @return the remove item
	 */
	private JMenuItem createRemoveItem(SaveList saveList)
	{
		JMenuItem itemRemove = new JMenuItem("Delete");

//		itemRemove.setIcon(IconFontSwing.buildIcon(FontAwesome.TIMES_CIRCLE, 17, Color.decode("0xea3622")));
		itemRemove.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		itemRemove.addActionListener(event -> {
			saveList.askToDeleteEntries(saveList.getSelectedValuesList());
		});
		return itemRemove;
	}


	/**
	 * Creates the 'Rename' item of the context menu.
	 * 
	 * @return the rename item
	 */
	private JMenuItem createEditItem(SaveList saveList)
	{
		JMenuItem itemEdit = new JMenuItem("Rename");
//		itemEdit.setIcon(IconFontSwing.buildIcon(Elusive.EDIT, 15, new Color(243, 156, 18)));
		itemEdit.setAccelerator(KeyStroke.getKeyStroke("F2"));
		itemEdit.addActionListener(event -> {
			saveList.askToEditEntry(saveList.getSelectedValue());
		});
		return itemEdit;
	}


	private JMenuItem createCopyItem(SaveList saveList)
	{
		JMenuItem itemCopy = new JMenuItem("Copy");
		itemCopy.setAccelerator(KeyStroke.getKeyStroke("control C"));
		itemCopy.addActionListener(event -> {
			saveList.copyEntries(false);
		});

		return itemCopy;
	}


	private JMenuItem createCutItem(SaveList saveList)
	{
		JMenuItem itemCut = new JMenuItem("Cut");
//		itemCut.setIcon(IconFontSwing.buildIcon(FontAwesome.SCISSORS, 17));
		itemCut.setAccelerator(KeyStroke.getKeyStroke("control X"));
		itemCut.addActionListener(event -> {
			saveList.copyEntries(true);
		});

		return itemCut;
	}


	private JMenuItem createPasteItem(SaveList saveList)
	{
		JMenuItem itemPaste = new JMenuItem("Paste");
		itemPaste.setAccelerator(KeyStroke.getKeyStroke("control V"));
		itemPaste.addActionListener(event -> {
			saveList.pasteEntries();
		});

		return itemPaste;
	}


	/**
	 * Creates the 'Read-Only' item of the context menu.
	 * 
	 * @return the read only item
	 */
	private JMenuItem createReadOnlyItem(SaveList saveList)
	{
		JMenuItem itemReadOnly = new JMenuItem("Enable 'Read-Only'");
		itemReadOnly.setIcon(IconsAndFontsManager.getReadOnlyIcon(IconsAndFontsManager.ICON_SIZE_MEDIUM, false));
		itemReadOnly.addActionListener(event -> {
			for (SaveListEntry entry : saveList.getSelectedValuesList())
			{
				if (!(entry instanceof Save))
					continue;
				File file = entry.getFile();
				file.setWritable(!isSelectionWritable);
			}
			if (OrganizerManager.getSelectedSortingCategory() == SortingCategory.READ_ONLY)
				saveList.refreshList();
			saveList.repaint();
		});
		return itemReadOnly;
	}


	private JMenuItem createRefreshItem(SaveList saveList)
	{
		JMenuItem itemRefresh = new JMenuItem("Refresh From File System");
		itemRefresh.setIcon(IconFontSwing.buildIcon(Elusive.REPEAT, 15, Color.decode("0x1d6fbe")));
		itemRefresh.addActionListener(event -> {
			saveList.refreshFromFileSystem(false);
		});
		return itemRefresh;
	}


	/**
	 * @return
	 */
	private JMenuItem createOpenInExplorerItem(SaveList saveList)
	{
		JMenuItem itemOpenInExplorer = new JMenuItem("Open Folder In Explorer");
//		itemOpenInExplorer.setIcon(IconFontSwing.buildIcon(Entypo.EXPORT, 15, new Color(30, 144, 255)));
		itemOpenInExplorer.addActionListener(event -> {
			SaveListEntry entry = saveList.getSelectedValue();
			File dirToOpen = GamesManager.getSelectedProfile().getRoot().getFile(); // default folder to open
			if (entry != null)
			{
				if (entry instanceof Folder)
					dirToOpen = entry.getFile();
				else
					dirToOpen = entry.getParent().getFile();
			}
			try
			{
				Desktop.getDesktop().open(dirToOpen);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "Error when trying to open the explorer!", "Error occurred", JOptionPane.ERROR_MESSAGE);
			}
		});
		return itemOpenInExplorer;
	}

}
