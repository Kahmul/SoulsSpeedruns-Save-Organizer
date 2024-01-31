package com.soulsspeedruns.organizer.main.ui;


import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.JComboBox;

import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.games.Profile;
import com.soulsspeedruns.organizer.listeners.ProfileListener;
import com.soulsspeedruns.organizer.managers.GamesManager;


/**
 * ProfilesComboBox.
 * <p>
 * ComboBox displaying Profile objects.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 29 Sep 2015
 */
public class ProfilesComboBox extends JComboBox<Profile> implements ProfileListener
{

	/**
	 * Creates a new ProfilesComboBox.
	 * 
	 * @param game the game associated with this combobox
	 */
	public ProfilesComboBox(Game game)
	{
		fillWith(game);

		GamesManager.addProfileListener(this);
		setRenderer(new ProfilesComboBoxRenderer());
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				GamesManager.switchToProfile((Profile) event.getItem());
			}
		});
	}


	/**
	 * Fills the combobox with the profiles of the given game.
	 * 
	 * @param game the game to get the profiles from
	 */
	private void fillWith(Game game)
	{
		removeAllItems();
		List<Profile> profiles = game.getProfiles();
		for (Profile profile : profiles)
		{
			addItem(profile);
			if (GamesManager.getSelectedProfile().equals(profile))
				setSelectedItem(profile);
		}
		if (profiles.size() == 0)
			GamesManager.switchToProfile(new Profile("", game));
	}


	/**
	 * Sets the game to retrieve the profiles from.
	 * 
	 * @param game the game to retrieve the profiles from
	 */
	public void setGame(Game game)
	{
		if (game != null)
			fillWith(game);
	}


	@Override
	public void profileDeleted(Profile profile)
	{
		if (GamesManager.getSelectedGame().equals(profile.getGame()))
		{
			removeItem(profile);
			// if the currently chosen profile is deleted, another one gets auto selected, without the Manager being updated. So
			// switchToProfile() is called to update it. This also indirectly calls repaint().
			GamesManager.switchToProfile((Profile) getSelectedItem());
		}
	}


	@Override
	public void profileCreated(Profile profile)
	{
		if (GamesManager.getSelectedGame().equals(profile.getGame()))
			fillWith(GamesManager.getSelectedGame());
	}


	@Override
	public void profileDirectoryChanged(Game game)
	{
		if (GamesManager.getSelectedGame().equals(game))
			fillWith(GamesManager.getSelectedGame());
	}


	@Override
	public void changedToProfile(Profile profile)
	{
		// updates the combobox UI when a profile is edited that is currently selected in the organizer, as changing the name
		// requires calling switchToProfile() to update the stored selected profile. As a result, this method is called as well,
		// so the combobox is repainted.
		repaint();
	}


	@Override
	public void changedToGame(Game game)
	{
	}

}
