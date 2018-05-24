package com.speedsouls.organizer.savelist;


import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.games.Game;


/**
 * GamesComboBox.
 * <p>
 * ComboBox displaying Game objects.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 29 Sep 2015
 */
public class GamesComboBox extends JComboBox<Game> implements ListCellRenderer<Game>
{

	private static final long serialVersionUID = -3428616630686103313L;


	/**
	 * Creates a new GamesComboBox.
	 * 
	 * @param games array of games to display in this combobox
	 * @param profilesCB the associated ProfilesComboBox
	 */
	public GamesComboBox(Game[] games, ProfilesComboBox profilesCB)
	{
		fillWith(games);

		setRenderer(this);
		setPrototypeDisplayValue(Game.DARK_SOULS_II_SOTFS);
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				Game game = (Game) event.getItem();
				OrganizerManager.switchToGame(game);
				profilesCB.setGame(game);
			}
		});
	}


	/**
	 * Fills the combobox with the given games.
	 * 
	 * @param games the games to fill with
	 */
	public void fillWith(Game[] games)
	{
		if (games != null && games.length > 0)
		{
			for (int i = 0; i < games.length; i++)
			{
				addItem(games[i]);
				if (OrganizerManager.getSelectedGame() == games[i])
				{
					setSelectedItem(games[i]);
				}
			}
		}
	}


	@Override
	public Component getListCellRendererComponent(JList<? extends Game> list, Game game, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, game, index, isSelected, cellHasFocus);
		if (game != null)
			label.setText(game.getCaption());
		return label;
	}

}
