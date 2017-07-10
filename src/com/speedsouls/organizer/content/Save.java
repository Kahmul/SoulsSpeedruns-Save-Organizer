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
	 * Creates a new Save instance.
	 * 
	 * @param parent the parent folder
	 * @param file the associated file
	 */
	public Save(Folder parent, File file)
	{
		super(parent, file);
	}


	@Override
	public int compareTo(SaveListEntry entry)
	{
		if (entry instanceof Folder)
			return 1;
		return OrganizerManager.getSelectedSortingCategory().compare(this, entry);
	}


	@Override
	public void rename(String newName)
	{
		getFile().renameTo(new File(getParent().getFile() + File.separator + newName));
		setFile(new File(getParent().getFile() + File.separator + newName));
	}


	@Override
	public void render(JLabel label)
	{
		label.setText(getFile().getName());
		label.setBorder(BorderFactory.createEmptyBorder(1, 3 + getIndent(), 0, 1));
		if (!getFile().canWrite())
			label.setIcon(new ImageIcon(OrganizerManager.readOnlyIconSmall));
	}


	@Override
	public void delete()
	{
		getParent().removeChild(this);
		getFile().delete();
	}

}
