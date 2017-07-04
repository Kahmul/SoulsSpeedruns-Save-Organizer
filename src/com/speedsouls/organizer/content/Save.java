package com.speedsouls.organizer.content;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import com.speedsouls.organizer.data.OrganizerManager;


/**
 * Savestate class.
 * <p>
 * Class representing the savestates and folders.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class Save implements Comparable<Save>, Transferable
{

	private File file;
	private String name;
	private int indent;
	private boolean isCollapsed;

	public static final DataFlavor SAVE_FLAVOR = new DataFlavor(Save.class, Save.class.getSimpleName());


	/**
	 * Creates a new Save object and associates it with the given file.
	 *
	 * @param file the file to associate this Save object with.
	 */
	public Save(File file)
	{
		this.file = file;
		this.name = file.getName();
		isCollapsed = true;

		File parent = file.getParentFile();
		indent = 0;
		while (!parent.equals(OrganizerManager.getSelectedProfile().getDirectory()))
		{
			indent += 20;
			parent = parent.getParentFile();
		}
	}


	/**
	 * Creates a new Save object, associates it with the given file and names it after the given name.
	 *
	 * @param file the file to associate this Save object with.
	 * @param name the name of this Save object.
	 */
	public Save(File file, String name)
	{
		this(file);
		this.name = name;
	}


	/**
	 * @return the associated file with this Save object.
	 */
	public File getFile()
	{
		return file;
	}


	/**
	 * @param file the file to associate this Save object with.
	 */
	public void setFile(File file)
	{
		this.file = file;
		File parent = file.getParentFile();
		indent = 0;
		while (!parent.equals(OrganizerManager.getSelectedProfile().getDirectory()))
		{
			indent += 20;
			parent = parent.getParentFile();
		}
	}


	/**
	 * Checks whether this save is one of the subcontents of the given save, no matter how deep in the structure.
	 * 
	 * @param save the save to check for
	 * @return True if this save is below the given save in the hierarchy. False otherwise.
	 */
	public boolean isSubContentOf(Save save)
	{
		File parent = file.getParentFile();
		while (!parent.equals(OrganizerManager.getSelectedProfile().getDirectory()))
		{
			if (parent.equals(save.getFile()))
				return true;
			parent = parent.getParentFile();
		}
		return false;
	}


	/**
	 * @return whether this savefile is read-only or not.
	 */
	public boolean isReadOnly()
	{
		return !file.canWrite();
	}


	/**
	 * Returns whether the save is a directory or not.
	 * 
	 * @return true if the save is a directory, false otherwise
	 */
	public boolean isDirectory()
	{
		return file.isDirectory();
	}


	/**
	 * @return the current display name.
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * @param name the name to display this Save object with.
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * The indent for the save list.
	 * 
	 * @return the indent
	 */
	public int getIndent()
	{
		return indent;
	}


	/**
	 * Sets the indent for the save list.
	 * 
	 * @param indent the indent to set
	 */
	public void setIndent(int indent)
	{
		this.indent = indent;
	}


	/**
	 * Returns whether this save is collapsed. Always returns true if this save is not a directory.
	 * 
	 * @return whether this save is collapsed or not
	 */
	public boolean isCollapsed()
	{
		if (!isDirectory())
			return true;
		return isCollapsed;
	}


	/**
	 * Closes/opens this save if it is a directory.
	 * 
	 * @param collapsed true or false
	 */
	public void setIsCollapsed(boolean collapsed)
	{
		if (!isDirectory())
			return;
		isCollapsed = collapsed;
	}


	@Override
	public int compareTo(Save save)
	{
		return OrganizerManager.getSelectedSortingCategory().compare(this, save);
	}


	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (isDataFlavorSupported(flavor))
			return this;
		throw new UnsupportedFlavorException(flavor);
	}


	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { SAVE_FLAVOR };
	}


	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(SAVE_FLAVOR);
	}

}
