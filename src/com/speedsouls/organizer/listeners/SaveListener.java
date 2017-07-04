package com.speedsouls.organizer.listeners;


import com.speedsouls.organizer.content.Save;


/**
 * Save Listener.
 * <p>
 * Provides methods to listen for save changes.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 1 Oct 2015
 */
public interface SaveListener
{

	/**
	 * Called when the selection in the SaveList changed.
	 * 
	 * @param save the save that was selected
	 */
	public void saveSelected(Save save);


	/**
	 * Called when a save was renamed.
	 * 
	 * @param save the save that was renamed
	 * @param newName the new name of the save
	 */
	public void saveRenamed(Save save, String newName);


	/**
	 * Called when the load process of a save has been started.
	 * 
	 * @param save the save that is loaded
	 */
	public void saveLoadStarted(Save save);


	/**
	 * Called when the load process of save was finished.
	 * 
	 * @param save the save that was loaded
	 */
	public void saveLoadFinished(Save save);


	/**
	 * Called when the user switched the current gamefile from writable to read-only and vice versa.
	 * 
	 * @param writable the writable state switched to
	 */
	public void gameFileWritableStateChanged(boolean writeable);

}
