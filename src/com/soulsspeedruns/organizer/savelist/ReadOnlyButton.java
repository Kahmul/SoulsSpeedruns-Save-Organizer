package com.soulsspeedruns.organizer.savelist;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JLabel;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.listeners.ProfileListener;
import com.soulsspeedruns.organizer.listeners.SaveListener;
import com.soulsspeedruns.organizer.profileconfig.Profile;


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
	public ReadOnlyButton(File file)
	{
		super(OrganizerManager.writableIcon22);

		setFile(file);
		addMouseListener(this);
		OrganizerManager.addProfileListener(this);
		OrganizerManager.addSaveListener(this);

		setVisible(OrganizerManager.getSelectedGame().supportsReadOnly());
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
			setVisible(false);
			setToolTipText(null);
			return;
		}
		this.file = file;
		setVisible(true);
		changeImage(file.canWrite(), false);
	}


	/**
	 * Simulates a click.
	 */
	public void doClick()
	{
		if (file == null || !file.exists() || !OrganizerManager.getSelectedGame().supportsReadOnly())
			return;
		boolean isWritable = !file.canWrite();
		file.setWritable(isWritable);
		changeImage(isWritable, true);
	}
	
	private void changeImage(boolean isWritable, boolean isHovering)
	{
		if(isWritable)
		{			
			if(isHovering)
				setIcon(OrganizerManager.writableIcon22Hover);
			else
				setIcon(OrganizerManager.writableIcon22);
			setToolTipText("Click to turn on read-only for the game's savefile.");
			setText("Writable");
		}
		else
		{
			if(isHovering)
				setIcon(OrganizerManager.readOnlyIcon22Hover);
			else
				setIcon(OrganizerManager.readOnlyIcon22);
			setToolTipText("Click to turn off read-only for the game's savefile.");
			setText("Read-Only");
		}

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		doClick();
	}


	@Override
	public void mouseEntered(MouseEvent e)
	{
		changeImage(file.canWrite(), true);
	}


	@Override
	public void mouseExited(MouseEvent e)
	{
		changeImage(file.canWrite(), false);
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
		if (file == null || !file.exists() || !game.supportsReadOnly())
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
		changeImage(save.getFile().canWrite(), false);
	}


	@Override
	public void gameFileWritableStateChanged(boolean writeable)
	{
		setEnabled(!writeable);
	}

}
