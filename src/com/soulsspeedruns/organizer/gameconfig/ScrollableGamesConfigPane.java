package com.soulsspeedruns.organizer.gameconfig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
	
	private JPanel gameSelectionPanel;


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
		
		JButton addButton = new JButton(IconFontSwing.buildIcon(FontAwesome.PLUS, 16, (Color) UIManager.get("hyperlink")));
		addButton.setBorderPainted(false);
		addButton.setBackground(buttonColor);
		addButton.setMargin(new Insets(6, 17, 6, 17));
		addButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		editButton = new JButton(IconFontSwing.buildIcon(FontAwesome.PENCIL, 16, (Color) UIManager.get("hyperlink")));
		editButton.setBorderPainted(false);
		editButton.setBackground(buttonColor);
		editButton.setMargin(new Insets(6, 17, 6, 17));
		editButton.setBorder(new EmptyBorder(0, 0, 0, 0));
//		editButton.setEnabled(false);
		
		JButton deleteButton = new JButton(IconFontSwing.buildIcon(FontAwesome.TRASH, 16, (Color) UIManager.get("hyperlink")));
		deleteButton.setBorderPainted(false);
		deleteButton.setBackground(buttonColor);
		deleteButton.setMargin(new Insets(6, 17, 6, 17));
		deleteButton.setBorder(new EmptyBorder(0, 0, 0, 0));
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
	
	
	private JPanel createGameSelectionPanel()
	{
		gameSelectionPanel = new JPanel();
		gameSelectionPanel.setLayout(new BoxLayout(gameSelectionPanel, BoxLayout.LINE_AXIS));
		
		gameSelectionPanel.add(createGameList());
		gameSelectionPanel.add(selectedEntry.getConfigPanel());
		
		return gameSelectionPanel;
	}
	
	private JScrollPane createGameList()
	{
		JScrollPane scrollPane = new JScrollPane();
		JPanel listPanel = new JPanel();

		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));

		List<Game> games = Game.GAMES;
		for (Game game : games)
		{
			GameListEntry entry = new GameListEntry(game, this);

			if (OrganizerManager.getSelectedGame().equals(game))
			{
				entry.setSelected(true);
				selectedEntry = entry;
			}

			entries.add(entry);
			listPanel.add(entry);
		}
		for (Game game : games)
		{
			listPanel.add(new GameListEntry(game, this));
		}
		
		scrollPane.setViewportView(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.setPreferredSize(new Dimension(150, 200));
		scrollPane.setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("borderSecondary")));
		
		return scrollPane;
	}
	
	
	protected GameListEntry getSelectedEntry()
	{
		return selectedEntry;
	}
	
	
	protected void setSelectedEntry(GameListEntry entry)
	{
		if (entry != selectedEntry)
		{
			if(selectedEntry != null)
			{
				selectedEntry.setSelected(false);
				gameSelectionPanel.remove(selectedEntry.getConfigPanel());
			}
			
			entry.setSelected(true);
			gameSelectionPanel.add(entry.getConfigPanel());
			selectedEntry = entry;
			
			revalidate();
			repaint();
		}
	}

}
