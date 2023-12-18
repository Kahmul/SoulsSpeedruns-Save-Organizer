package com.soulsspeedruns.organizer.listeners;

/**
 * Settings Listener.
 * <p>
 * Provides methods to listen for changes to the settings.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 18 Dec 2023
 */
public interface SettingsListener
{

	/**
	 * Called when the user navigates to next element
	 */
	public void settingChanged(String prefsKey);
}
