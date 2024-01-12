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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
 * Component pane offering all available games in a scrollable list, together
 * with a configuration panel and buttons to add/delete games.
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

	JScrollPane scrollPane;
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
		addButton.addActionListener((e) -> {
			addEntry();
		});

		editButton = createButton(FontAwesome.PENCIL, buttonColor);
//		editButton.setEnabled(false);

		deleteButton = createButton(FontAwesome.TRASH, buttonColor);
		deleteButton.addActionListener((e) -> {
			deleteSelectedEntry();
		});
//		deleteButton.setEnabled(false);

		JLabel settingsLabel = new JLabel("Game Settings");

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
		JButton button = new JButton(IconFontSwing.buildIcon(icon, 16, (Color) UIManager.get("hyperlink")));
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
				.addComponent(configPanel);

		GroupLayout.ParallelGroup vGroup = gameSelectionLayout.createParallelGroup().addComponent(gameList,
				GroupLayout.PREFERRED_SIZE, configPanel.getPreferredSize().height, GroupLayout.PREFERRED_SIZE)
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
			{
				entry.setSelected(true);
				editButton.setEnabled(entry.getGame().isCustomGame());
				deleteButton.setEnabled(entry.getGame().isCustomGame());

				selectedEntry = entry;
			}

			entries.add(entry);
			listPanel.add(entry);
		}

		for (int i = 0; i < 5; i++)
		{
			Game nier = Game.createGame("NieR: Automata", "NRA", "test.sl2", true, true);
			GameListEntry nierEntry = new GameListEntry(nier, this);
			entries.add(nierEntry);
			listPanel.add(nierEntry);
		}

//		for (Game game : games)
//		{
//			listPanel.add(new GameListEntry(game, this));
//		}

		scrollPane.setViewportView(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("borderSecondary")));

		return scrollPane;
	}
	
	
	protected void addEntry()
	{
		Game nier = Game.createGame("NieR: Automata", "NRA", "test.sl2", true, true);
		GameListEntry nierEntry = new GameListEntry(nier, this);
		entries.add(nierEntry);
		listPanel.add(nierEntry);
		
		setSelectedEntry(nierEntry);
		
		scrollPane.validate();
		scrollPane.getVerticalScrollBar().setValue(nierEntry.getLocation().y);
	}


	protected void deleteSelectedEntry()
	{
		int selectedIndex = entries.indexOf(selectedEntry);
		int newIndex = Math.max(0, entries.indexOf(selectedEntry) - 1);

		entries.remove(selectedIndex);
		listPanel.remove(selectedIndex);

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
			selectedEntry.setSelected(false);
			entry.setSelected(true);

			gameSelectionLayout.replace(selectedEntry.getConfigPanel(), entry.getConfigPanel());
			selectedEntry = entry;

			editButton.setEnabled(selectedEntry.getGame().isCustomGame());
			deleteButton.setEnabled(selectedEntry.getGame().isCustomGame());

			revalidate();
			repaint();
		}
	}

}
