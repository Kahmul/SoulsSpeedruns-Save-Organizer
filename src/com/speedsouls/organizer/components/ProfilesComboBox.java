package com.speedsouls.organizer.components;


import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.Profile;
import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.listeners.ProfileListener;


/**
 * ProfilesComboBox.
 * <p>
 * ComboBox displaying Profile objects.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 29 Sep 2015
 */
public class ProfilesComboBox extends JComboBox<Profile> implements ListCellRenderer<Profile>, ProfileListener
{

	private static final long serialVersionUID = 1056591949253213932L;


	/**
	 * Creates a new ProfilesComboBox.
	 * 
	 * @param game the game associated with this combobox
	 */
	public ProfilesComboBox(Game game)
	{
		fillWith(game);

		OrganizerManager.addProfileListener(this);
		setRenderer(this);
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				OrganizerManager.switchToProfile((Profile) event.getItem());
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
			if (OrganizerManager.getSelectedProfile().equals(profile))
				setSelectedItem(profile);
		}
		if (profiles.size() == 0)
			OrganizerManager.switchToProfile(new Profile("", game));
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
	public Component getListCellRendererComponent(JList<? extends Profile> list, Profile profile, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, profile, index, isSelected, cellHasFocus);
		if (profile != null)
			label.setText(profile.getName());
		return label;
	}


	@Override
	public void profileDeleted(Profile profile)
	{
		if (OrganizerManager.getSelectedGame().equals(profile.getGame()))
		{
			repaint();
			removeItem(profile);
		}
	}


	@Override
	public void changedToProfile(Profile profile)
	{
	}


	@Override
	public void changedToGame(Game game)
	{
	}

}
