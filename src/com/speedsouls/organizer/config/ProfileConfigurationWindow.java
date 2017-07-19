package com.speedsouls.organizer.config;


import java.awt.Dialog;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.data.OrganizerManager;


/**
 * Window for configuring profiles.
 * <p>
 * Allows the configuration of existing and creation of new profiles.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class ProfileConfigurationWindow extends JDialog
{

	private static final long serialVersionUID = 649337266477016499L;


	/**
	 * Creates a new configuration window.
	 */
	public ProfileConfigurationWindow()
	{
		super(OrganizerManager.getMainWindow(), "Profile Configuration", Dialog.ModalityType.APPLICATION_MODAL);

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
		setLocationRelativeTo(null);
		setIconImage(OrganizerManager.speedsoulsIcon);
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());
	}


	/**
	 * Adds all components to the layout.
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		Game[] games = Game.values();
		for (Game game : games)
		{
			GameConfigPanel panel = new GameConfigPanel(game);
			tabbedPane.add(panel, game.getCaption());
			if (OrganizerManager.getSelectedGame().equals(game))
				tabbedPane.setSelectedComponent(panel);
		}
		guiPanel.add(tabbedPane);

		add(guiPanel);
	}

}
