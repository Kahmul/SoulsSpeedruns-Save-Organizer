package com.soulsspeedruns.organizer.games.config;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;


/**
 * Game List
 * <p>
 * A scrollable list of games.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jan 2024
 */
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

			if (GamesManager.getSelectedGame().equals(game))
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


	/**
	 * Creates a new game along with an entry in the list with the given game name and savefile name
	 * 
	 * @param gameName the name of the game to add
	 * @param saveName the game's savefile name
	 */
	protected void addEntry(String gameName, String saveName)
	{
		int gameID = GamesManager.getNewCustomGameID();
		if (gameID == -1)
		{
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "Error when saving new custom game. Backing store unavailable.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Game newGame = Game.createGame(gameName, String.valueOf(gameID), saveName, null, null, true, true);
		GameListEntry newGameEntry = new GameListEntry(newGame, this);
		entries.add(newGameEntry);
		listPanel.add(newGameEntry);

		setSelectedEntry(newGameEntry);

		SettingsManager.storeGameProperties(newGame);

		validate();
		getVerticalScrollBar().setValue(newGameEntry.getLocation().y);
	}


	/**
	 * Updates the name or savefile name of an existing game.
	 * 
	 * @param gameName the new game name
	 * @param saveName the new savefile name
	 */
	protected void updateEntry(String gameName, String saveName)
	{
		Game selectedGame = selectedEntry.getGame();
		selectedGame.setCaption(gameName);
		selectedGame.setSaveName(saveName);

		selectedEntry.setText(gameName);

		SettingsManager.storeGameProperties(selectedGame);

		fireEntryUpdatedEvent(selectedEntry);

		revalidate();
		repaint();
	}


	/**
	 * Deletes the currently selected entry.
	 */
	protected void deleteSelectedEntry()
	{
		int selectedIndex = entries.indexOf(selectedEntry);
		int newIndex = Math.max(0, entries.indexOf(selectedEntry) - 1);

		entries.remove(selectedIndex);
		listPanel.remove(selectedIndex);

		Game.deleteGame(selectedEntry.getGame());

		setSelectedEntry(entries.get(newIndex));
	}


	/**
	 * Get the currently selected entry in this list
	 * 
	 * @return the selected entry
	 */
	protected GameListEntry getSelectedEntry()
	{
		return selectedEntry;
	}


	/**
	 * Sets the selected entry to the give entry.
	 *
	 * @param entry the entry to select
	 */
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


	/**
	 * Clears any potential drop targets for any potentially ongoing drag operations.
	 */
	protected void clearDropTarget()
	{
		if (dropTarget != null)
			dropTarget.setIsDropTarget(false, false);
		dropTarget = null;
	}


	/**
	 * Highlights the entry at the given index as a potential target to drop another list entry onto.
	 * 
	 * @param index the index of the drop target
	 */
	protected void setDropTargetIndex(int index)
	{
		boolean isEndOfList = false;
		if (index < 0)
			index = 0;
		if (index >= entries.size())
		{
			index = entries.size() - 1;
			isEndOfList = true;
		}

		if (dropTarget != null)
			dropTarget.setIsDropTarget(false, isEndOfList);

		dropTarget = entries.get(index);
		if (dropTarget != null)
			dropTarget.setIsDropTarget(true, isEndOfList);

	}


	/**
	 * Returns the drop index of a potential drop operation based on the given point in the list.
	 * 
	 * @param p the point in the list
	 * @return the index of a potential drop operation
	 */
	protected int getDropIndexByPoint(Point p)
	{
		for (int i = 0; i < entries.size(); i++)
		{
			Rectangle rect = entries.get(i).getBounds();

			Rectangle upperHalf = new Rectangle(rect.x, rect.y, rect.width, rect.height / 2);
			Rectangle lowerHalf = new Rectangle(rect.x, rect.y + rect.height / 2, rect.width, rect.height / 2);
			if (upperHalf.contains(p))
				return i;
			if (lowerHalf.contains(p))
				return i + 1;
		}

		return entries.size();
	}


	/**
	 * Moves an existing entry at the old index to a new index in the list.
	 * 
	 * @param oldIndex the old index of the entry to move
	 * @param newIndex the new index for the entry to move
	 */
	protected void moveIndexToNewIndex(int oldIndex, int newIndex)
	{
		GameListEntry entryToMove = entries.get(oldIndex);
		Game gameToMove = entryToMove.getGame();

		listPanel.remove(oldIndex);
		entries.remove(entryToMove);

		newIndex = oldIndex > newIndex ? newIndex : Math.max(0, newIndex - 1);

		listPanel.add(entryToMove, newIndex);
		entries.add(newIndex, entryToMove);
		Game.moveGame(gameToMove, newIndex);

		validate();
		repaint();
	}


	protected int getIndexOfEntry(GameListEntry entry)
	{
		return entries.indexOf(entry);
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
