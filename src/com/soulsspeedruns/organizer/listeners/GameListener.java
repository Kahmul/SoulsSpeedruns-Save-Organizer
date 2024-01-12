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
	
}
