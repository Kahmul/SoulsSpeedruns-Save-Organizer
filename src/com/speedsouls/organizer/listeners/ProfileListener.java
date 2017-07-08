package com.speedsouls.organizer.listeners;


import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.Profile;


/**
 * Profile Listener.
 * <p>
 * Provides methods to listen for profile changes.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 29 Sep 2015
 */
public interface ProfileListener
{

	/**
	 * Called when the profiles for this game are updated.
	 * 
	 * @param game the game the profiles were updated for
	 */
	public void profilesUpdated(Game game);


	/**
	 * Called when the user switches to another profile.
	 * 
	 * @param profile the profile that was switched to
	 */
	public void changedToProfile(Profile profile);


	/**
	 * Called when the user switches to another game.
	 * 
	 * @param game the game that was switched to
	 */
	public void changedToGame(Game game);

}
