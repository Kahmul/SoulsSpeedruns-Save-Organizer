package com.soulsspeedruns.organizer.main.config;


import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.JComboBox;

import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.listeners.GameListener;
import com.soulsspeedruns.organizer.managers.GamesManager;


/**
 * GamesComboBox.
 * <p>
 * ComboBox displaying Game objects.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 29 Sep 2015
 */
public class GamesComboBox extends JComboBox<Game> implements GameListener
{

	private boolean updateProfileComboBox = true;

	/**
	 * Creates a new GamesComboBox.
	 * 
	 * @param profilesCB the associated ProfilesComboBox
	 */
	public GamesComboBox(ProfilesComboBox profilesCB)
	{
		fillWith(Game.GAMES);

		setRenderer(new GamesComboBoxRenderer());
		setPrototypeDisplayValue(Game.DARK_SOULS_II_SOTFS);
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED && updateProfileComboBox)
			{
				Game game = (Game) event.getItem();
				GamesManager.switchToGame(game);
				profilesCB.setGame(game);
			}
		});
		
		GamesManager.addGameListener(this);
	}


	/**
	 * Fills the combobox with the given games.
	 * 
	 * @param games the games to fill with
	 */
	public void fillWith(List<Game> games)
	{
		removeAllItems();
		if (games != null && games.size() > 0)
		{
			for (int i = 0; i < games.size(); i++)
			{
				addItem(games.get(i));
				if (GamesManager.getSelectedGame() == games.get(i))
				{
					setSelectedItem(games.get(i));
				}
			}
		}
	}


	@Override
	public void gameCreated(Game game)
	{
		addItem(game);
	}


	@Override
	public void gameDeleted(Game game)
	{
		removeItem(game);
		if(GamesManager.getSelectedGame() == game)
			setSelectedIndex(Math.max(0, getSelectedIndex() - 1));
	}


	@Override
	public void gameEdited(Game game)
	{
		
	}
	
	@Override
	public void gameMoved(Game game, int newIndex)
	{
		Game selectedGame = (Game) getSelectedItem();
		
		updateProfileComboBox = false;
		
		removeItem(game);
		insertItemAt(game, newIndex);
		
		setSelectedItem(selectedGame);
		
		updateProfileComboBox = true;
	}

}
