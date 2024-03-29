package com.soulsspeedruns.organizer.savelist;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.SavesManager;

import jiconfont.icons.Iconic;
import jiconfont.swing.IconFontSwing;


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
	
	private static final Icon ICON_FILE_DOES_NOT_EXIST = IconFontSwing.buildIcon(Iconic.CHECK, 13, Color.RED);

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
		return SavesManager.getSelectedSortingCategory().compare(this, entry);
	}


	@Override
	public boolean rename(String newName)
	{
		File newFile = new File(getParent().getFile() + File.separator + newName);
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
		return true;
	}


	@Override
	public boolean canBeRenamed()
	{
		return getFile().canWrite();
	}


	@Override
	public void render(JList<? extends SaveListEntry> list, int index, JLabel label)
	{
		label.setText(getFile().getName());
		label.setBorder(BorderFactory.createEmptyBorder(1, 3 + getIndent(), 0, 1));
		if(isMarkedForCut())
			label.setForeground(Color.GRAY);
		if (!getFile().canWrite())
			label.setIcon(IconsAndFontsManager.getReadOnlyIcon(IconsAndFontsManager.ICON_SIZE_SMALL, false));
		if (!getFile().exists())
		{
			label.setIcon(ICON_FILE_DOES_NOT_EXIST);
			label.setForeground(Color.RED);
			label.setToolTipText("File no longer exists!");
		}
	}


	@Override
	public void delete()
	{
		getParent().removeChild(this);
		getFile().delete();
	}

}
