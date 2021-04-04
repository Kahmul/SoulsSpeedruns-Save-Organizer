package com.speedsouls.organizer.savelist;


import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.speedsouls.organizer.data.OrganizerManager;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * Folder class.
 * <p>
 * Represents the folders in the SaveList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 7 Jul 2017
 */
public class Folder extends SaveListEntry
{

	private boolean isClosed = true;
	private static final Color ICON_COLOR = new Color(251, 208, 108);
	private static final int ICON_SIZE = 15;


	/**
	 * Creates a new Folder instance.
	 * 
	 * @param parent the parent folder
	 * @param file the associated file
	 */
	public Folder(Folder parent, File file)
	{
		super(parent, file);
		File[] files = file.listFiles();
		for (File currentFile : files)
		{
			if (currentFile.isDirectory())
			{
				addChild(new Folder(this, currentFile));
				continue;
			}
			addChild(new Save(this, currentFile));
		}
		Collections.sort(getChildren());
	}


	/**
	 * @return whether the folder is closed or not
	 */
	public boolean isClosed()
	{
		return isClosed;
	}


	/**
	 * @param isClosed set the folder state closed or opened
	 */
	public void setClosed(boolean isClosed)
	{
		this.isClosed = isClosed;
	}


	@Override
	public void removeChild(SaveListEntry entry)
	{
		super.removeChild(entry);
		if (getChildren().size() == 0)
			setClosed(true);
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
		for (SaveListEntry entry : getChildren())
		{
			// call rename on all children with the same name to update the path with the new parent
			if (!entry.rename(entry.getName()))
				return false;
		}
		return true;
	}


	@Override
	public boolean canBeRenamed()
	{
		if (getFile().canWrite())
		{
			for (SaveListEntry entry : getChildren())
			{
				if (!entry.canBeRenamed())
					return false;
			}
			return true;
		}
		return false;
	}


	@Override
	public void delete()
	{
		getParent().removeChild(this);
		OrganizerManager.deleteDirectory(getFile());
	}


	@Override
	public void render(JLabel label)
	{
		label.setText(getFile().getName());
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setBorder(BorderFactory.createEmptyBorder(1, 3 + getIndent(), 0, 1));
		Color color = ICON_COLOR;
		if (!getFile().exists())
		{
			color = Color.RED;
			label.setForeground(Color.RED);
			label.setToolTipText("Directory does not exist any longer!");
		}
		if (isClosed())
			label.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER, ICON_SIZE, color));
		else
			label.setIcon(IconFontSwing.buildIcon(FontAwesome.FOLDER_OPEN, ICON_SIZE, color));
	}


	@Override
	public int compareTo(SaveListEntry entry)
	{
		if (entry instanceof Save)
			return -1;
		return OrganizerManager.getSelectedSortingCategory().compare(this, entry);
	}
}
