package com.speedsouls.organizer.listeners;


import com.speedsouls.organizer.content.SortingCategory;


/**
 * Sorting Listener.
 * <p>
 * Provides methods to listen for sorting category changes.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 20 May 2016
 */
public interface SortingListener
{

	/**
	 * Called when the user chooses a different sorting option.
	 * 
	 * @param category the category that was chosen
	 */
	public void sortingChanged(SortingCategory category);

}
