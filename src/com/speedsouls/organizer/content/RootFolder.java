package com.speedsouls.organizer.content;


import java.io.File;

import com.speedsouls.organizer.data.OrganizerManager;


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


	/*
	 * @see com.speedsouls.organizer.content.SaveListEntry#rename(java.lang.String)
	 */
	@Override
	public void rename(String newName)
	{
		File newFile = new File(OrganizerManager.getSelectedGame().getDirectory() + File.separator + newName);
		getFile().renameTo(newFile);
		setFile(newFile);
		for (SaveListEntry entry : getChildren())
		{
			// call rename on all children with the same name to update the path with the new parent
			entry.rename(entry.getName());
		}
	}


	/*
	 * @see com.speedsouls.organizer.content.SaveListEntry#delete()
	 */
	@Override
	public void delete()
	{
		OrganizerManager.deleteDirectory(getFile());
	}

}
