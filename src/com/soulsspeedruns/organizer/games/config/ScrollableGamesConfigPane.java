package com.soulsspeedruns.organizer.games.config;


import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * Scrollable Games Config Pane
 * <p>
 * Component pane offering all available games in a scrollable list, together with a configuration panel and buttons to add/delete games.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 11 Jan 2024
 */
public class ScrollableGamesConfigPane extends JPanel
{

	private final List<GameListEntry> entries = new ArrayList<>();
	private GameListEntry selectedEntry;

	private JButton editButton;
	private JButton deleteButton;
	private JLabel settingsLabel;

	private JScrollPane scrollPane;
	private JPanel listPanel;

	private GroupLayout gameSelectionLayout;


	public ScrollableGamesConfigPane()
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
			new GameCreationWindow(this, null);
		});

		editButton = createButton(FontAwesome.PENCIL, buttonColor);
		editButton.addActionListener(e -> {
			new GameCreationWindow(this, selectedEntry.getGame());
		});

		deleteButton = createButton(FontAwesome.TRASH, buttonColor);
		deleteButton.addActionListener((e) -> {
			int confirm = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(this),
					"Do you really want to delete " + selectedEntry.getGame().getCaption() + "?", "Delete Custom Game", JOptionPane.YES_NO_OPTION);
			if (confirm == 0)
				deleteSelectedEntry();
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

		JScrollPane gameList = createGameList();
		GameConfigPanel configPanel = selectedEntry.getConfigPanel();

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


	private JScrollPane createGameList()
	{
		scrollPane = new JScrollPane();
		listPanel = new JPanel();

		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));

		List<Game> games = Game.GAMES;
		for (Game game : games)
		{
			GameListEntry entry = new GameListEntry(game, this);

			if (OrganizerManager.getSelectedGame().equals(game))
				setSelectedEntry(entry);

			entries.add(entry);
			listPanel.add(entry);
		}

		scrollPane.setViewportView(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("borderSecondary")));

		return scrollPane;
	}


	protected void addEntry(String gameName, String saveName)
	{
		Game nier = Game.createGame(gameName, String.valueOf(Game.GAMES.size()), saveName, null, null, true, true);
		GameListEntry nierEntry = new GameListEntry(nier, this);
		entries.add(nierEntry);
		listPanel.add(nierEntry);

		setSelectedEntry(nierEntry);

		scrollPane.validate();
		scrollPane.getVerticalScrollBar().setValue(nierEntry.getLocation().y);
	}


	protected void updateEntry(String gameName, String saveName)
	{
		Game selectedGame = selectedEntry.getGame();
		selectedGame.setCaption(gameName);
		selectedGame.setSaveName(saveName);

		selectedEntry.setText(gameName);
		settingsLabel.setText(gameName);

		revalidate();
		repaint();
	}


	protected void deleteSelectedEntry()
	{
		int selectedIndex = entries.indexOf(selectedEntry);
		int newIndex = Math.max(0, entries.indexOf(selectedEntry) - 1);

		entries.remove(selectedIndex);
		listPanel.remove(selectedIndex);

		Game.deleteGame(selectedEntry.getGame());

		setSelectedEntry(entries.get(newIndex));
	}


	protected GameListEntry getSelectedEntry()
	{
		return selectedEntry;
	}


	protected void setSelectedEntry(GameListEntry entry)
	{
		if (entry != selectedEntry)
		{
			if (selectedEntry != null)
			{
				selectedEntry.setSelected(false);
				gameSelectionLayout.replace(selectedEntry.getConfigPanel(), entry.getConfigPanel());
			}

			entry.setSelected(true);
			selectedEntry = entry;

			editButton.setEnabled(selectedEntry.getGame().isCustomGame());
			deleteButton.setEnabled(selectedEntry.getGame().isCustomGame());
			settingsLabel.setText(selectedEntry.getGame().getCaption());

			revalidate();
			repaint();
		}
	}

}
