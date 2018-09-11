package com.speedsouls.organizer.listeners;

/**
 * Navigation Listener.
 * <p>
 * Provides methods to listen for upwards/downwards navigation.
 *
 * @author johndisandonato (www.twitch.tv/johndisandonato)
 * @date 05 Sep 2018
 */
public interface NavigationListener
{

	/**
	 * Called when the user navigates to previous element
	 */
	public void navigatedToPrevious();

	/**
	 * Called when the user navigates to next element
	 */
	public void navigatedToNext();
}
