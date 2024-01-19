package com.soulsspeedruns.organizer.games.config;


import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;


/**
 * Window for configuring games.
 * <p>
 * Allows the configuration of existing and creation of new games and their profiles.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class GamesConfigurationWindow extends JDialog
{

	/**
	 * Creates a new configuration window.
	 */
	public GamesConfigurationWindow()
	{
		super(OrganizerManager.getMainWindow(), "Games Configuration", Dialog.ModalityType.APPLICATION_MODAL);

		initLayout();
		initProperties();

		setVisible(true);
	}


	/**
	 * Sets the properties of the window.
	 */
	private void initProperties()
	{
		pack();
		setResizable(false);
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setIconImage(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_MEDIUM));
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowActivated(WindowEvent e)
			{
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.updateComponentTreeUI(GamesConfigurationWindow.this);
				});
			}
		});
	}


	/**
	 * Adds all components to the layout.
	 */
	private void initLayout()
	{
		GamesConfigPane gamesPane = new GamesConfigPane();

		gamesPane.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocusInWindow();
			}
		});

		add(gamesPane);
	}

}
