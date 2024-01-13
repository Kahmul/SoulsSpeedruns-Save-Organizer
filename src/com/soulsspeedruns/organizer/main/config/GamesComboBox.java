package com.soulsspeedruns.organizer.main.config;


import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.listeners.GameListener;


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

	protected final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();


	/**
	 * Creates a new GamesComboBox.
	 * 
	 * @param games array of games to display in this combobox
	 * @param profilesCB the associated ProfilesComboBox
	 */
	public GamesComboBox(List<Game> games, ProfilesComboBox profilesCB)
	{
		fillWith(games);

		setRenderer(new GamesComboBoxRenderer());
		setPrototypeDisplayValue(Game.DARK_SOULS_II_SOTFS);
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				Game game = (Game) event.getItem();
				OrganizerManager.switchToGame(game);
				profilesCB.setGame(game);
			}
		});
		
		OrganizerManager.addGameListener(this);
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
				if (OrganizerManager.getSelectedGame() == games.get(i))
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
		if(OrganizerManager.getSelectedGame() == game)
			setSelectedIndex(Math.max(0, getSelectedIndex() - 1));
	}


	@Override
	public void gameEdited(Game game)
	{
		
	}

}