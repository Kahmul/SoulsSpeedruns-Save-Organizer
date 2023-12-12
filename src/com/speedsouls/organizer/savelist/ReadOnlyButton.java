package com.speedsouls.organizer.savelist;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.games.Game;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;
import com.speedsouls.organizer.profileconfig.Profile;


/**
 * Read Only Button.
 * <p>
 * Button to make the given file read-only and vice-versa.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class ReadOnlyButton extends JLabel implements MouseListener, ProfileListener, SaveListener
{

	private static final long serialVersionUID = -4432217286267536787L;
	private File file;


	/**
	 * Creates a new read only button with the given file and image.
	 * 
	 * @param file the file to associate with this button.
	 * @param img the image to use for this button.
	 */
	public ReadOnlyButton(File file, ImageIcon img)
	{
		super(img);

		setFile(file);
		addMouseListener(this);
		OrganizerManager.addProfileListener(this);
		OrganizerManager.addSaveListener(this);

		if (OrganizerManager.getSelectedGame().equals(Game.DARK_SOULS_REMASTERED))
			setVisible(false);
	}


	/**
	 * Returns the file that is associated with this button.
	 * 
	 * @return the file that is associated with this button
	 */
	public File getFile()
	{
		return file;
	}


	/**
	 * Sets the file to associate this button with.
	 * 
	 * @param file the file to associate this button with
	 */
	public void setFile(File file)
	{
		if (file == null || !file.exists())
		{
			this.file = null;
			setEnabled(false);
			setToolTipText(null);
			return;
		}
		this.file = file;
		setEnabled(!file.canWrite());
	}


	/**
	 * Simulates a click.
	 */
	public void doClick()
	{
		if (file == null || !file.exists() || OrganizerManager.getSelectedGame().equals(Game.DARK_SOULS_REMASTERED))
			return;
		setEnabled(!isEnabled());
		file.setWritable(!isEnabled());
	}


	@Override
	public void setEnabled(boolean flag)
	{
		super.setEnabled(flag);
		if (flag)
			setToolTipText("The current gamefile is read-only.");
		else
			setToolTipText("The current gamefile is writable.");
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
		doClick();
	}


	@Override
	public void mouseEntered(MouseEvent e)
	{
	}


	@Override
	public void mouseExited(MouseEvent e)
	{
	}


	@Override
	public void mousePressed(MouseEvent e)
	{
	}


	@Override
	public void mouseReleased(MouseEvent e)
	{
	}


	@Override
	public void profileDeleted(Profile profile)
	{
	}


	@Override
	public void profileCreated(Profile profile)
	{
	}


	@Override
	public void profileDirectoryChanged(Game game)
	{
	}


	@Override
	public void changedToProfile(Profile profile)
	{
	}


	@Override
	public void changedToGame(Game game)
	{
		setFile(game.getSaveFileLocation());
		if (game.equals(Game.DARK_SOULS_REMASTERED))
			setVisible(false);
		else
			setVisible(true);
	}


	@Override
	public void entryCreated(SaveListEntry entry)
	{
	}


	@Override
	public void entryRenamed(SaveListEntry entry)
	{
	}


	@Override
	public void entrySelected(SaveListEntry entry)
	{
	}


	@Override
	public void saveLoadStarted(Save save)
	{
	}


	@Override
	public void saveLoadFinished(Save save)
	{
		setEnabled(!save.getFile().canWrite());
	}


	@Override
	public void gameFileWritableStateChanged(boolean writeable)
	{
		setEnabled(!writeable);
	}

}
