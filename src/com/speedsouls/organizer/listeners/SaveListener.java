package com.speedsouls.organizer.listeners;


import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.content.SaveListEntry;


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
	public void entrySelected(SaveListEntry entry);


	/**
	 * Called when a save is imported or a folder is created.
	 * 
	 * @param entry the entry that was created
	 */
	public void entryCreated(SaveListEntry entry);


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
