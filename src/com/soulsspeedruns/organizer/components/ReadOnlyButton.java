package com.soulsspeedruns.organizer.components;


import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.UIManager;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.event.ThemeChangeEvent;
import com.github.weisj.darklaf.theme.event.ThemeChangeListener;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.games.Profile;
import com.soulsspeedruns.organizer.listeners.ProfileListener;
import com.soulsspeedruns.organizer.listeners.SaveListener;
import com.soulsspeedruns.organizer.listeners.SettingsListener;
import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SavesManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.managers.VersionManager;
import com.soulsspeedruns.organizer.savelist.Save;
import com.soulsspeedruns.organizer.savelist.SaveListEntry;


/**
 * Read Only Button.
 * <p>
 * Button to make the given file read-only and vice-versa.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class ReadOnlyButton extends JButton implements MouseListener, ProfileListener, SaveListener, SettingsListener
{

	private File file;


	/**
	 * Creates a new read only button with the given file and image.
	 * 
	 * @param file      the file to associate with this button.
	 * @param isCompact whether to show the "writable/read-only" text
	 */
	public ReadOnlyButton(File file)
	{
		setFile(file);
		
		setForeground(new Color(UIManager.getColor("textForeground").getRGB()));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		setMargin(new Insets(0, 0, 0, 0));

		addMouseListener(this);
		GamesManager.addProfileListener(this);
		SavesManager.addSaveListener(this);
		SettingsManager.addSettingsListener(this);
		
		addActionListener(e -> {
			doClick();
		});

		LafManager.addThemeChangeListener(new ThemeChangeListener()
		{

			@Override
			public void themeInstalled(ThemeChangeEvent e)
			{
				refreshAppearance(false);
				setForeground(new Color(UIManager.getColor("textForeground").getRGB()));
			}


			@Override
			public void themeChanged(ThemeChangeEvent e)
			{
			}
		});

		OrganizerManager.getMainWindow().addComponentListener(new ComponentAdapter()
		{

			public void componentResized(ComponentEvent e)
			{
				refreshAppearance(false);
			}
		});

		setVisible(true);
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
		refreshAppearance(false);
	}


	/**
	 * Simulates a click.
	 */
	public void doClick()
	{
		if (file == null || !file.exists() || !GamesManager.getSelectedGame().supportsReadOnly())
			return;
		file.setWritable(!file.canWrite());
		refreshAppearance(true);
	}


	/**
	 * Changes the image of the read-only button depending on the state of the file and whether the mouse is hovered over it.
	 * 
	 * @param isHovering
	 */
	private void refreshAppearance(boolean isHovering)
	{
		boolean isWritable = file != null ? file.canWrite() : false;
		boolean showText = !SettingsManager.isCompactModeEnabled()
				&& (!VersionManager.isVersionOutdated() || OrganizerManager.getMainWindow().getWidth() > 700);
		if (isWritable)
		{
			setText(showText ? "Writable" : null);
			setIcon(IconsAndFontsManager.getWritableIcon(IconsAndFontsManager.ICON_SIZE_LARGE, isHovering));
			setToolTipText("Click to turn on read-only for the game's savefile.");
		}
		else
		{
			setText(showText ? "Read-Only" : null);
			setIcon(IconsAndFontsManager.getReadOnlyIcon(IconsAndFontsManager.ICON_SIZE_LARGE, isHovering));
			setToolTipText("Click to turn off read-only for the game's savefile.");
		}

	}


	@Override
	public void setVisible(boolean flag)
	{
		super.setVisible(flag);

		if (flag)
		{
			if (!GamesManager.getSelectedGame().supportsReadOnly())
				super.setVisible(false);
			if (!GamesManager.isAProfileSelected())
				super.setVisible(false);
		}

	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
	}


	@Override
	public void mouseEntered(MouseEvent e)
	{
		refreshAppearance(true);
	}


	@Override
	public void mouseExited(MouseEvent e)
	{
		refreshAppearance(false);
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
		setFile(game.getSaveFileLocation());
		if (file == null || !file.exists() || !game.supportsReadOnly())
			setVisible(false);
		else
			setVisible(true);
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
		refreshAppearance(false);
	}


	@Override
	public void gameFileWritableStateChanged(boolean writeable)
	{
		refreshAppearance(false);
	}


	@Override
	public void settingChanged(String prefsKey)
	{
		refreshAppearance(false);
	}

}
