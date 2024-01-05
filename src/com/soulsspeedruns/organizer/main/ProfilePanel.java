package com.soulsspeedruns.organizer.main;


import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.mainconfig.GamesComboBox;
import com.soulsspeedruns.organizer.mainconfig.ProfilesComboBox;
import com.soulsspeedruns.organizer.profileconfig.ProfileConfigurationWindow;


/**
 * Top segment of the main window.
 * <p>
 * Contains the ComboBoxes and 'Edit Profiles' button.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class ProfilePanel extends JPanel
{

	private static final long serialVersionUID = 726071248080811470L;


	/**
	 * Creates a new ProfilePanel for the main window.
	 */
	protected ProfilePanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel gamesLabel = new JLabel("Game:");
		JLabel profilesLabel = new JLabel("Profile:");

		ProfilesComboBox profilesComboBox = createProfilesComboBox();
		GamesComboBox gamesComboBox = createGameComboBox(profilesComboBox);

		JButton editButton = createEditProfilesButton();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(gamesComboBox).addComponent(gamesLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(profilesComboBox).addComponent(profilesLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(editButton));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(gamesLabel).addComponent(profilesLabel));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(gamesComboBox).addComponent(profilesComboBox)
				.addComponent(editButton));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}


	/**
	 * Creates the games' ComboBox.
	 * 
	 * @param profilesComboBox the profiles' ComboBox
	 * @return the GamesComboBox
	 */
	private GamesComboBox createGameComboBox(ProfilesComboBox profilesComboBox)
	{
		GamesComboBox gamesComboBox = new GamesComboBox(Game.values(), profilesComboBox);
		gamesComboBox.setMaximumSize(new Dimension(80, 20));
		return gamesComboBox;
	}


	/**
	 * Creates the profiles' ComboBox.
	 * 
	 * @return the ProfilesComboBox
	 */
	private ProfilesComboBox createProfilesComboBox()
	{
		ProfilesComboBox profilesComboBox = new ProfilesComboBox(OrganizerManager.getSelectedGame());
		profilesComboBox.setMinimumSize(new Dimension(175, 20));
		return profilesComboBox;
	}


	/**
	 * Creates the 'Edit profiles' button.
	 * 
	 * @return the edit profiles button
	 */
	private JButton createEditProfilesButton()
	{
		JButton editButton = new JButton("Edit profiles");
		editButton.addActionListener(event -> {
			new ProfileConfigurationWindow();
		});
		return editButton;
	}

}
