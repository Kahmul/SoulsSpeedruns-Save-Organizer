package com.soulsspeedruns.organizer.games.config;


import java.awt.Color;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * Games Config Pane
 * <p>
 * Component pane offering all available games in a scrollable list, together with a configuration panel and buttons to add/delete games.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 11 Jan 2024
 */
public class GamesConfigPane extends JPanel implements GameListListener
{

	private JButton editButton;
	private JButton deleteButton;
	private JLabel settingsLabel;

	private GroupLayout gameSelectionLayout;
	private GameList gameList;


	public GamesConfigPane()
	{
		super();

		initLayout();
	}


	private void initLayout()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(createButtonsPanel());
		add(createGameSelectionPanel());
	}


	private JPanel createButtonsPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

		Color buttonColor = new Color(UIManager.getColor("background").getRGB());

		JButton addButton = createButton(FontAwesome.PLUS, buttonColor);
		addButton.addActionListener(e -> {
			new GameCreationWindow(gameList, null);
		});

		editButton = createButton(FontAwesome.PENCIL, buttonColor);
		editButton.addActionListener(e -> {
			new GameCreationWindow(gameList, gameList.getSelectedEntry().getGame());
		});

		deleteButton = createButton(FontAwesome.TRASH, buttonColor);
		deleteButton.addActionListener((e) -> {
			int confirm = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(this),
					"Do you really want to delete your custom game '" + gameList.getSelectedEntry().getGame().getCaption() + "'?",
					"Delete Custom Game", JOptionPane.YES_NO_OPTION);
			if (confirm == 0)
				gameList.deleteSelectedEntry();
		});

		settingsLabel = new JLabel("Game Settings");

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(settingsLabel);
		buttonPanel.add(Box.createHorizontalGlue());

		buttonPanel.setBorder(new MatteBorder(0, 0, 1, 0, UIManager.getColor("borderSecondary")));

		return buttonPanel;
	}


	private JButton createButton(FontAwesome icon, Color buttonColor)
	{
		JButton button = new JButton(IconFontSwing.buildIcon(icon, 16, UIManager.getColor("gameConfigButtonColors")));
		button.setDisabledIcon(IconFontSwing.buildIcon(icon, 16, UIManager.getColor("disabledIconColor")));
		button.setBorderPainted(false);
		button.setBackground(buttonColor);
		button.setMargin(new Insets(6, 18, 6, 18));
		button.setBorder(new EmptyBorder(0, 0, 0, 0));

		return button;
	}


	private JPanel createGameSelectionPanel()
	{
		JPanel gameSelectionPanel = new JPanel();

		gameSelectionLayout = new GroupLayout(gameSelectionPanel);

		gameList = new GameList();
		gameList.addListener(this);
		updateButtonState(gameList.getSelectedEntry());

		GameConfigPanel configPanel = gameList.getSelectedEntry().getConfigPanel();

		GroupLayout.SequentialGroup hGroup = gameSelectionLayout.createSequentialGroup()
				.addComponent(gameList, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
				.addComponent(configPanel, GroupLayout.PREFERRED_SIZE, 435, GroupLayout.PREFERRED_SIZE);

		GroupLayout.ParallelGroup vGroup = gameSelectionLayout.createParallelGroup()
				.addComponent(gameList, GroupLayout.PREFERRED_SIZE, configPanel.getPreferredSize().height, GroupLayout.PREFERRED_SIZE)
				.addComponent(configPanel);

		gameSelectionLayout.setHorizontalGroup(hGroup);
		gameSelectionLayout.setVerticalGroup(vGroup);

		gameSelectionPanel.setLayout(gameSelectionLayout);

		return gameSelectionPanel;
	}


	/**
	 * Updates the state of the menu buttons to reflect the given entry.
	 * 
	 * @param entry
	 */
	private void updateButtonState(GameListEntry entry)
	{
		editButton.setEnabled(entry.getGame().isCustomGame());
		deleteButton.setEnabled(entry.getGame().isCustomGame());
		settingsLabel.setText(entry.getGame().getCaption());
	}


	/**
	 * Called when the games configuration window is opened to init values.
	 */
	protected void loadConfigPane()
	{
		gameList.updateScrollbar();
	}


	@Override
	public void entryCreated(GameListEntry entry)
	{

	}


	@Override
	public void entryUpdated(GameListEntry entry)
	{
		settingsLabel.setText(entry.getGame().getCaption());

		revalidate();
		repaint();
	}


	@Override
	public void entryDeleted(GameListEntry entry)
	{

	}


	@Override
	public void entrySelected(GameListEntry prevEntry, GameListEntry newEntry)
	{
		if (prevEntry != null)
			gameSelectionLayout.replace(prevEntry.getConfigPanel(), newEntry.getConfigPanel());

		updateButtonState(newEntry);

		revalidate();
		repaint();
	}

}
