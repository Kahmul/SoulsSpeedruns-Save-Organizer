package com.soulsspeedruns.organizer.listeners;


/**
 * Search Listener.
 * <p>
 * Provides methods to listen for search requests.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 19 May 2016
 */
public interface SearchListener
{

	/**
	 * Called when a search request has been sent.
	 * 
	 * @param input the search input
	 */
	public void searchRequested(String input);

}
