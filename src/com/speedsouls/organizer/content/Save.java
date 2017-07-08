package com.speedsouls.organizer.content;


import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.speedsouls.organizer.data.OrganizerManager;


/**
 * Savestate class.
 * <p>
 * Class representing the savestates and folders.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class Save extends SaveListEntry
{

	/**
	 * @param parent
	 * @param file
	 */
	public Save(Folder parent, File file)
	{
		super(parent, file);
	}


	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SaveListEntry entry)
	{
		if (entry instanceof Folder)
			return 1;
		return OrganizerManager.getSelectedSortingCategory().compare(this, entry);
	}


	/*
	 * @see com.speedsouls.organizer.content.SaveListEntry#rename(java.lang.String)
	 */
	@Override
	public void rename(String newName)
	{
		getFile().renameTo(new File(getParent().getFile() + File.separator + newName));
		setFile(new File(getParent().getFile() + File.separator + newName));
	}


	/*
	 * @see com.speedsouls.organizer.content.SaveListEntry#render(javax.swing.JLabel)
	 */
	@Override
	public void render(JLabel label)
	{
		label.setText(getFile().getName());
		label.setBorder(BorderFactory.createEmptyBorder(1, 3 + getIndent(), 0, 1));
		if (!getFile().canWrite())
			label.setIcon(new ImageIcon(OrganizerManager.readOnlyIconSmall));
	}


	/*
	 * @see com.speedsouls.organizer.content.SaveListEntry#delete()
	 */
	@Override
	public void delete()
	{
		getParent().removeChild(this);
		getFile().delete();
	}

}
