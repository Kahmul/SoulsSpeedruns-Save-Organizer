package com.soulsspeedruns.organizer.games.ui;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.soulsspeedruns.organizer.games.Game;


/**
 * Game Configuration Panel.
 * <p>
 * Panel containing components to configure game settings and their respective
 * profiles.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 28 Sep 2015
 */
public class GameConfigPanel extends JPanel
{


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
