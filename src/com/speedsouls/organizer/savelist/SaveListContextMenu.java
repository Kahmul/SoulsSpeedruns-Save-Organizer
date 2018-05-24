package com.speedsouls.organizer.savelist;


import java.awt.Color;
import java.awt.Desktop;
import java.awt.Point;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.games.Game;

import jiconfont.icons.Elusive;
import jiconfont.icons.Entypo;
import jiconfont.icons.Iconic;
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

	private static final long serialVersionUID = -1904622827514676246L;


	/**
	 * Creates and shows a new context menu for the given SaveList.
	 * 
	 * @param saveList the savelist to create this menu for
	 * @param x the x coordinate to create the menu at
	 * @param y the y coordinate to create the menu at
	 */
	public SaveListContextMenu(SaveList saveList, int x, int y)
	{
		super();

		JMenuItem itemAdd = createAddItem(saveList);
		JMenuItem itemRemove = createRemoveItem(saveList);
		JMenuItem itemEdit = createEditItem(saveList);
		JCheckBoxMenuItem itemReadOnly = createReadOnlyItem(saveList);
		JMenuItem itemRefresh = createRefreshItem(saveList);
		JMenuItem itemOpenInExplorer = createOpenInExplorerItem(saveList);

		add(itemAdd);
		add(itemRemove);
		add(itemEdit);
		if (!OrganizerManager.getSelectedGame().equals(Game.DARK_SOULS_REMASTERED))
			add(itemReadOnly);
		add(itemRefresh);
		add(itemOpenInExplorer);

		show(saveList, x, y);

		if (OrganizerManager.getSelectedProfile().getName().equals(""))
		{
			itemAdd.setEnabled(false);
			itemOpenInExplorer.setEnabled(false);
		}
		int index = saveList.locationToIndex(new Point(x, y));
		if (index != -1 && saveList.getCellBounds(index, index).contains(new Point(x, y)))
		{
			saveList.setSelectedIndex(index);
			itemEdit.setEnabled(true);
			itemRemove.setEnabled(true);
			itemReadOnly.setEnabled(saveList.getSelectedValue() instanceof Save);
			itemReadOnly.setSelected(!saveList.getSelectedValue().getFile().canWrite());
			return;
		}
		itemEdit.setEnabled(false);
		itemRemove.setEnabled(false);
		itemReadOnly.setEnabled(false);
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
		itemAdd.setIcon(IconFontSwing.buildIcon(Elusive.PLUS_SIGN, 15, new Color(39, 174, 96)));
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
		itemRemove.setIcon(IconFontSwing.buildIcon(Iconic.CHECK, 18, new Color(231, 76, 60)));
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
		itemEdit.setIcon(IconFontSwing.buildIcon(Elusive.EDIT, 15, new Color(243, 156, 18)));
		itemEdit.setAccelerator(KeyStroke.getKeyStroke("F2"));
		itemEdit.addActionListener(event -> {
			saveList.askToEditEntry(saveList.getSelectedValue());
		});
		return itemEdit;
	}


	/**
	 * Creates the 'Read-Only' item of the context menu.
	 * 
	 * @return the read only item
	 */
	private JCheckBoxMenuItem createReadOnlyItem(SaveList saveList)
	{
		JCheckBoxMenuItem itemReadOnly = new JCheckBoxMenuItem("Read-Only");
		itemReadOnly.addActionListener(event -> {
			saveList.getSelectedValue().getFile().setWritable(!itemReadOnly.isSelected());
			if (OrganizerManager.getSelectedSortingCategory() == SortingCategory.READ_ONLY)
				saveList.sortEntries();
			saveList.repaint();
		});
		return itemReadOnly;
	}


	private JMenuItem createRefreshItem(SaveList saveList)
	{
		JMenuItem itemRefresh = new JMenuItem("Refresh From File System");
		itemRefresh.setIcon(IconFontSwing.buildIcon(Elusive.REPEAT, 15, new Color(138, 43, 226)));
		itemRefresh.addActionListener(event -> {
			saveList.refresh();
		});
		return itemRefresh;
	}


	/**
	 * @return
	 */
	private JMenuItem createOpenInExplorerItem(SaveList saveList)
	{
		JMenuItem itemOpenInExplorer = new JMenuItem("Open Folder In Explorer");
		itemOpenInExplorer.setIcon(IconFontSwing.buildIcon(Entypo.EXPORT, 15, new Color(30, 144, 255)));
		itemOpenInExplorer.addActionListener(event -> {
			SaveListEntry entry = saveList.getSelectedValue();
			File dirToOpen = OrganizerManager.getSelectedProfile().getRoot().getFile(); // default folder to open
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
				JOptionPane.showMessageDialog(null, "Error when trying to open the explorer!", "Error occured", JOptionPane.ERROR_MESSAGE);
			}
		});
		return itemOpenInExplorer;
	}

}
