package com.soulsspeedruns.organizer.listeners;


import com.soulsspeedruns.organizer.games.Game;


/**
 * Game Listener.
 * <p>
 * Provides methods to listen for game changes.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 12 Jan 2024
 */
public interface GameListener
{

	/**
	 * Called when a new game has been created.
	 * 
	 * @param game the game that was created
	 */
	public void gameCreated(Game game);

	/**
	 * Called when a game has been deleted.
	 * 
	 * @param game the game that was deleted.
	 */
	public void gameDeleted(Game game);

	/**
	 * Called when a game has been edited or changed.
	 * 
	 * @param game the game that was edited or changed.
	 */
	public void gameEdited(Game game);

	/**
	 * Called when a game's order in the list has been changed.
	 * 
	 * @param game     the game that was moved
	 * @param newIndex the index it was moved to
	 */
	public void gameMoved(Game game, int newIndex);

	/**
	 * Called when the process for a game has been hooked into.
	 * 
	 * @param game the game for which the process was hooked into
	 */
	public void gameProcessHooked(Game game);

	/**
	 * Called when the hook for a game process was removed.
	 * 
	 * @param game the game for which the process hook was removed
	 */
	public void gameProcessUnhooked(Game game);

}
