/**
 * 
 */
package com.soulsspeedruns.organizer.main;


import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.soulsspeedruns.organizer.components.SearchBar;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.games.ui.GamesConfigurationWindow;
import com.soulsspeedruns.organizer.listeners.GameListener;
import com.soulsspeedruns.organizer.main.ui.GamesComboBox;
import com.soulsspeedruns.organizer.main.ui.ProfilesComboBox;
import com.soulsspeedruns.organizer.main.ui.SortingComboBox;
import com.soulsspeedruns.organizer.managers.GamesManager;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * Top segment of the main window.
 * <p>
 * Contains the games and profile comboboxes, as well as the Edit Games button and search bar.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 21 Jan 2024
 */
public class TopPanel extends JPanel
{

	/**
	 * Creates the top panel for the main window.
	 */
	protected TopPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

//		JLabel gamesLabel = new JLabel("Game:");

		JPanel gameLabelPanel = createGameLabelPanel();
		JLabel profilesLabel = new JLabel("Profile:");

		SearchBar searchBar = new SearchBar();

		ProfilesComboBox profilesComboBox = new ProfilesComboBox(GamesManager.getSelectedGame());
		GamesComboBox gamesComboBox = new GamesComboBox(profilesComboBox);

		JPanel sortingPanel = createSortingPanel();
		JPanel profilesPanel = createProfilesPanel(profilesComboBox);

		layout.linkSize(SwingConstants.HORIZONTAL, gamesComboBox, sortingPanel);

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(gameLabelPanel)
				.addComponent(gamesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(sortingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
		hGroup.addGroup(
				layout.createParallelGroup().addComponent(profilesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(profilesLabel).addComponent(searchBar));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(gameLabelPanel).addComponent(profilesLabel));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(gamesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(profilesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(sortingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(searchBar));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}


	/**
	 * Creates the panel with the Game label and the icon signifying the game process was hooked.
	 * 
	 * @return the game label panel
	 */
	private JPanel createGameLabelPanel()
	{
		JLabel gameLabel = new JLabel("Game:");

		JLabel hookedIcon = new JLabel(IconFontSwing.buildIcon(FontAwesome.LINK, 14, Color.GRAY));
		hookedIcon.setToolTipText("The organizer has successfully hooked into the game process.");
		hookedIcon.setBorder(new EmptyBorder(0, 0, 0, 3));
		hookedIcon.setVisible(false);

		GamesManager.addGameListener(new GameListener()
		{

			@Override
			public void gameProcessUnhooked(Game game)
			{
				hookedIcon.setVisible(false);
			}


			@Override
			public void gameProcessHooked(Game game)
			{
				hookedIcon.setVisible(true);
			}


			@Override
			public void gameMoved(Game game, int newIndex)
			{
			}


			@Override
			public void gameEdited(Game game)
			{
			}


			@Override
			public void gameDeleted(Game game)
			{
			}


			@Override
			public void gameCreated(Game game)
			{
			}
		});

		JPanel sortingPanel = new JPanel();
		GroupLayout panelLayout = new GroupLayout(sortingPanel);

		panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup().addComponent(hookedIcon).addComponent(gameLabel));
		panelLayout.setVerticalGroup(panelLayout.createParallelGroup(Alignment.CENTER).addComponent(gameLabel).addComponent(hookedIcon));

		sortingPanel.setLayout(panelLayout);

		return sortingPanel;
	}


	/**
	 * Creates the panel containing the sorting combobox and label.
	 * 
	 * @return the sorting panel
	 */
	private JPanel createSortingPanel()
	{
		JLabel sortByLabel = new JLabel("Sort by:");
		SortingComboBox sortingComboBox = new SortingComboBox();

		JPanel sortingPanel = new JPanel();
		GroupLayout panelLayout = new GroupLayout(sortingPanel);

		panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup().addComponent(sortByLabel).addGap(5).addComponent(sortingComboBox));
		panelLayout.setVerticalGroup(panelLayout.createParallelGroup(Alignment.CENTER).addComponent(sortByLabel).addComponent(sortingComboBox));

		sortingPanel.setLayout(panelLayout);

		return sortingPanel;
	}


	/**
	 * Creates the panel containing the profiles combobox and Edit Games button.
	 * 
	 * @param profilesComboBox the profiles combobox of this top panel
	 * @return the profiles panel
	 */
	private JPanel createProfilesPanel(ProfilesComboBox profilesComboBox)
	{
		JButton editButton = createEditProfilesButton();

		JPanel profilesPanel = new JPanel();
		GroupLayout panelLayout = new GroupLayout(profilesPanel);

		panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup()
				.addComponent(profilesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addGap(5)
				.addComponent(editButton));
		panelLayout.setVerticalGroup(panelLayout.createParallelGroup()
				.addComponent(profilesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(editButton));

		profilesPanel.setLayout(panelLayout);

		return profilesPanel;
	}


	/**
	 * Creates the 'Edit profiles' button.
	 * 
	 * @return the edit profiles button
	 */
	private JButton createEditProfilesButton()
	{
		JButton editButton = new JButton("Edit Games");
		editButton.addActionListener(event -> {
			new GamesConfigurationWindow();
		});
		return editButton;
	}

}
