package com.soulsspeedruns.organizer.games.config;


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;


public class GameList extends JScrollPane
{

	private final List<GameListEntry> entries = new ArrayList<>();
	private GameListEntry selectedEntry;
	private GameListEntry dropTarget;

	private JPanel listPanel;

	private List<GameListListener> listeners = new ArrayList<>();

	private final TransferHandler transferHandler = new GameListTransferHandler(this);


	public GameList()
	{
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

		setViewportView(listPanel);
		getVerticalScrollBar().setUnitIncrement(10);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("borderSecondary")));
		setTransferHandler(transferHandler);
	}


	protected void addEntry(String gameName, String saveName)
	{
		Game newGame = Game.createGame(gameName, String.valueOf(Game.GAMES.size()), saveName, null, null, true, true);
		GameListEntry newGameEntry = new GameListEntry(newGame, this);
		entries.add(newGameEntry);
		listPanel.add(newGameEntry);

		setSelectedEntry(newGameEntry);

		validate();
		getVerticalScrollBar().setValue(newGameEntry.getLocation().y);
	}


	protected void updateEntry(String gameName, String saveName)
	{
		Game selectedGame = selectedEntry.getGame();
		selectedGame.setCaption(gameName);
		selectedGame.setSaveName(saveName);

		selectedEntry.setText(gameName);

		fireEntryUpdatedEvent(selectedEntry);

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
				selectedEntry.setSelected(false);

			entry.setSelected(true);

			fireEntrySelectedEvent(selectedEntry, entry);

			selectedEntry = entry;

			revalidate();
			repaint();
		}
	}


	protected void setDropTargetEntry(GameListEntry entry)
	{
		if (dropTarget != null)
			dropTarget.setIsDropTarget(false);

		dropTarget = entry;
		if (dropTarget != null)
			dropTarget.setIsDropTarget(true);
	}


	protected GameListEntry getEntryByPoint(Point p)
	{
		for (GameListEntry entry : entries)
		{
			if (entry.getBounds().contains(p))
				return entry;
		}

		return entries.getLast();
	}


	public void addListener(GameListListener listener)
	{
		listeners.add(listener);
	}


	private void fireEntrySelectedEvent(GameListEntry prevEntry, GameListEntry newEntry)
	{
		for (GameListListener listener : listeners)
		{
			listener.entrySelected(prevEntry, newEntry);
		}
	}


	private void fireEntryUpdatedEvent(GameListEntry entry)
	{
		for (GameListListener listener : listeners)
		{
			listener.entryUpdated(entry);
		}
	}

}
