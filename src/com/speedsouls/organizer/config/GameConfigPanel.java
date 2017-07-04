package com.speedsouls.organizer.config;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.speedsouls.organizer.content.Game;


/**
 * Game Configuration Panel.
 * <p>
 * Panel containing components to configure game settings and their respective profiles.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 28 Sep 2015
 */
public class GameConfigPanel extends JPanel
{

	private static final long serialVersionUID = -1150088331110967812L;


	/**
	 * Creates a game configuration panel.
	 * 
	 * @param game the game to create the panel for
	 */
	protected GameConfigPanel(Game game)
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(new GameConfigDirectoryPanel(game));
		add(new JSeparator());
		add(new GameConfigProfilesPanel(game));
	}

}
