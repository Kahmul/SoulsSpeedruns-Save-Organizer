package com.speedsouls.organizer.dragndrop;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import com.speedsouls.organizer.components.SaveList;
import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.data.OrganizerManager;


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


	public boolean canImport(TransferHandler.TransferSupport support)
	{
		if (!support.isDataFlavorSupported(Save.SAVE_FLAVOR))
			return false;
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

		int index = saveList.locationToIndex(dl.getDropPoint());
		if (index == -1)
			return false;
		try
		{
			Save save = (Save) support.getTransferable().getTransferData(Save.SAVE_FLAVOR);
			File parentFile;
			if (!saveList.getCellBounds(index, index).contains(dl.getDropPoint()))
			{
				parentFile = OrganizerManager.getSelectedProfile().getDirectory();
				saveList.setDropMode(DropMode.INSERT);
			}
			else
			{
				parentFile = saveList.getModel().getElementAt(index).getFile();
				saveList.setDropMode(DropMode.ON);
				if (!parentFile.isDirectory())
				{
					parentFile = parentFile.getParentFile();
					saveList.setDropMode(DropMode.INSERT);
				}
			}
			if (parentFile.equals(save.getFile()))
				return false;
			Save parentSave = saveList.getSaveByFile(parentFile);
			if (parentSave != null && parentSave.isSubContentOf(save))
				return false;
			if (parentFile.equals(save.getFile().getParentFile()))
				return false;
			support.setShowDropLocation(true);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}


	public boolean importData(TransferHandler.TransferSupport support)
	{
		if (!canImport(support))
			return false;
		try
		{
			Save save = (Save) support.getTransferable().getTransferData(Save.SAVE_FLAVOR);

			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			File parentFile = findParentFileFromDropLocation(dl);

			Path newPath = Paths.get(parentFile.getPath()).resolve(save.getName());
			if (newPath.toFile().exists())
			{
				if (JOptionPane.showConfirmDialog(saveList.getParent(),
						save.getName() + " already exists in that directory. Do you want to overwrite?", "Confirmation",
						JOptionPane.YES_NO_OPTION) != 0)
					return false;
			}
			DefaultListModel<Save> model = (DefaultListModel<Save>) saveList.getModel();
			boolean wasCollapsed = save.isCollapsed();
			List<Save> subFolders = null;
			if (!wasCollapsed)
			{
				subFolders = saveList.findOpenSubFolders(save);
				saveList.closeDirectory(save);
			}
			File saveFile = Files.move(Paths.get(save.getFile().getPath()), newPath, StandardCopyOption.REPLACE_EXISTING).toFile();

			model.removeElement(save);
			saveList.sort();
			Save newDir = saveList.getSaveByFile(parentFile);
			if (newDir != null && newDir.isCollapsed())
				saveList.openDirectory(newDir);
			if (!wasCollapsed)
				reopenClosedSubFolders(model, subFolders, saveFile);
			saveList.setSelectedValue(saveList.getSaveByFile(saveFile), true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * Reopens the in the reordering process closed subfolders.
	 * 
	 * @param model savelist model
	 * @param subFolders the subfolders that were closed
	 * @param saveFile the parent save of the subfolders
	 */
	private void reopenClosedSubFolders(DefaultListModel<Save> model, List<Save> subFolders, File saveFile)
	{
		Save newSave = saveList.getSaveByFile(saveFile);
		saveList.openDirectory(newSave);
		for (Save subFolder : subFolders)
		{
			for (int i = model.indexOf(newSave); i < model.size(); i++)
			{
				if (model.getElementAt(i).getName().equals(subFolder.getFile().getName()))
				{
					saveList.openDirectory(model.getElementAt(i));
					break;
				}
			}
		}
	}


	/**
	 * Finds the parent file to the given location in the list.
	 * 
	 * @param dl the location
	 * @return the parent file
	 */
	private File findParentFileFromDropLocation(JList.DropLocation dl)
	{
		File parentFile;
		int index = saveList.locationToIndex(dl.getDropPoint());
		if (!saveList.getCellBounds(index, index).contains(dl.getDropPoint()))
			parentFile = OrganizerManager.getSelectedProfile().getDirectory();
		else
		{
			parentFile = saveList.getModel().getElementAt(index).getFile();
			if (!parentFile.isDirectory())
				parentFile = parentFile.getParentFile();
		}
		return parentFile;
	}

}
