package com.soulsspeedruns.organizer.savelist;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import com.soulsspeedruns.organizer.managers.OrganizerManager;


/**
 * RootFolder class.
 * <p>
 * Class representing the root folders of every profile. These folders don't have a parent.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 8 Jul 2017
 */
public class RootFolder extends Folder
{

	/**
	 * 
	 * @param parent
	 * @param file
	 */
	public RootFolder(File file)
	{
		super(null, file);
	}


	@Override
	public boolean rename(String newName)
	{
		File newFile = new File(OrganizerManager.getSelectedGame().getDirectory() + File.separator + newName);
		try
		{
			// if the same name is given, then only the file variable is supposed to be updated for a new parent
			if (!getFile().getName().equals((newName)))
				Files.move(getFile().toPath(), newFile.toPath());
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Renaming the entries was not successful. They are possibly being accessed by another program.", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		setFile(newFile);
		for (SaveListEntry entry : getChildren())
		{
			// call rename on all children with the same name to update the path with the new parent
			if (!entry.rename(entry.getName()))
				return false;
		}
		return true;
	}
	
	@Override
	public Folder getParent() {
		return this;
	}


	@Override
	public void delete()
	{
		OrganizerManager.deleteDirectory(getFile());
	}

}
