package com.speedsouls.organizer.dragndrop;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.savelist.Folder;
import com.speedsouls.organizer.savelist.SaveList;
import com.speedsouls.organizer.savelist.SaveListEntry;


/**
 * SaveListTransferHandler
 * <p>
 * TransferHandler for SaveList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 May 2016
 */
public class SaveListTransferHandler extends TransferHandler
{

	private static final long serialVersionUID = 4773499372263177613L;

	private SaveList saveList;


	/**
	 * Creates a new TransferHandler for a SaveList.
	 */
	public SaveListTransferHandler(SaveList saveList)
	{
		this.saveList = saveList;
	}


	@Override
	public boolean canImport(TransferHandler.TransferSupport support)
	{
		if (!support.isDataFlavorSupported(SaveListEntry.ENTRY_FLAVOR))
			return false;
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

		int index = saveList.locationToIndex(dl.getDropPoint());
		if (index == -1)
			return false;
		try
		{
			SaveListEntry entry = (SaveListEntry) support.getTransferable().getTransferData(SaveListEntry.ENTRY_FLAVOR);
			SaveListEntry newParentFolder = findNewParentFolderFromDropLocation(dl);
			if (entry.equals(newParentFolder))
				return false;
			if (entry.isParentOf(newParentFolder))
				return false;
			if (entry.getParent().equals(newParentFolder))
				return false;
			support.setShowDropLocation(true);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}


	@Override
	public boolean importData(TransferHandler.TransferSupport support)
	{
		if (!canImport(support))
			return false;
		try
		{
			SaveListEntry entry = (SaveListEntry) support.getTransferable().getTransferData(SaveListEntry.ENTRY_FLAVOR);

			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			Folder newParentFolder = findNewParentFolderFromDropLocation(dl);

			Path newPath = Paths.get(newParentFolder.getFile().getPath()).resolve(entry.getName());
			if (newPath.toFile().exists())
			{
				if (JOptionPane.showConfirmDialog(saveList.getParent(),
						entry.getName() + " already exists in that directory. Do you want to overwrite?", "Confirmation",
						JOptionPane.YES_NO_OPTION) != 0)
					return false;
				SaveListEntry existingEntry = newParentFolder.getChildByName(entry.getName());
				((DefaultListModel<SaveListEntry>) saveList.getModel()).removeElement(existingEntry);
				existingEntry.delete();
			}
			entry.setFile(Files.move(Paths.get(entry.getFile().getPath()), newPath, StandardCopyOption.REPLACE_EXISTING).toFile());
			entry.attachToNewParent(newParentFolder);
			newParentFolder.setClosed(false);
			saveList.sortEntries();
			saveList.setSelectedValue(entry, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * Finds the new parent folder at the given location in the list.
	 * 
	 * @param dl the droplocation
	 * @return the parent folder
	 */
	private Folder findNewParentFolderFromDropLocation(JList.DropLocation dl)
	{
		int index = saveList.locationToIndex(dl.getDropPoint());
		SaveListEntry newParentFolder;
		if (!saveList.getCellBounds(index, index).contains(dl.getDropPoint()))
		{
			newParentFolder = OrganizerManager.getSelectedProfile().getRoot();
			saveList.setDropMode(DropMode.INSERT);
		}
		else
		{
			newParentFolder = saveList.getModel().getElementAt(index);
			saveList.setDropMode(DropMode.ON);
			if (!(saveList.getModel().getElementAt(index) instanceof Folder))
			{
				newParentFolder = newParentFolder.getParent();
				saveList.setDropMode(DropMode.INSERT);
			}
		}
		return (Folder) newParentFolder;
	}

}
