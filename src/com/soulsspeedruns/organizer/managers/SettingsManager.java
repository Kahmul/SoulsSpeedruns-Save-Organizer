/**
 * 
 */
package com.soulsspeedruns.organizer.managers;


import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.jnativehook.NativeHookException;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.Theme;
import com.github.weisj.darklaf.theme.spec.ColorToneRule;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.hotkeys.GlobalHotkey;
import com.soulsspeedruns.organizer.hotkeys.GlobalKeyboardHook;
import com.soulsspeedruns.organizer.listeners.SettingsListener;
import com.soulsspeedruns.organizer.theme.DefaultTheme;
import com.soulsspeedruns.organizer.theme.SoulsSpeedrunsTheme;


/**
 * SettingsManager.
 * <p>
 * Manages the (de)activation and permanent storage of settings and game properties set by the user.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 20 Jan 2024
 */
public class SettingsManager
{

	/**
	 * Constants for paths to preferences and resources.
	 */
	private static final String PREFERENCES_PATH = "/com/soulsspeedruns/organizer/prefs";
	private static final String PREFERENCES_PATH_LEGACY = "/com/speedsouls";

	/**
	 * Constants for the keys used to access preferences.
	 */
	private static final String PREFS_KEY_INITIAL_STARTUP = "initStartup";
	private static final String PREFS_KEY_VERSION = "Version";

	private static final String PREFS_KEY_THEME = "Theme";

	private static final String PREFS_KEY_WIN_WIDTH = "WindowWidth";
	private static final String PREFS_KEY_WIN_HEIGHT = "WindowHeight";

	private static final String PREFS_KEY_MAXIMIZED = "WindowMaximized";

	private static final String PREFS_KEY_SELECTED_GAME = "selectedGame";
	private static final String PREFS_KEY_SELECTED_PROFILE = "selectedProfile";
	private static final String PREFS_KEY_SELECTED_SORTING = "selectedSorting";

	private static final String PREFS_KEY_SETTING_ALWAYS_ON_TOP = "alwaysOnTop";
	private static final String PREFS_KEY_SETTING_GLOBAL_HOTKEYS = "globalHotkeys";
	private static final String PREFS_KEY_SETTING_DOUBLE_CLICK_LOAD = "doubleClickLoad";
	private static final String PREFS_KEY_SETTING_CHECK_FOR_UPDATES = "checkForUpdates";
	private static final String PREFS_KEY_SETTING_COMPACT_MODE = "compactMode";

	public static final String PREFS_KEY_GLOBAL_HOTKEY_LOAD = "hotkeyLoad";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_READ_ONLY = "hotkeyReadOnly";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_IMPORT_SAVE = "hotkeyImportSave";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_PREV_SAVE = "hotkeyPrevSave";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_NEXT_SAVE = "hotkeyNextSave";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_TOGGLE = "hotkeyToggle";

	private static final String PREFS_MODIFIER_GAME_DIR = "Path";
	private static final String PREFS_MODIFIER_GAME_SAVEFILE = "Savefile";

	private static final String PREFS_MODIFIER_GAME_GAMENAME = "GameName";
	private static final String PREFS_MODIFIER_GAME_SAVENAME = "SaveName";
	private static final String PREFS_MODIFIER_GAME_INDEX = "Index";

	private static final String PREFS_PREFIX_CUSTOM_GAME = "CustomGame_";

	private static final String PREFS_ERROR_ON_RETRIEVE = "ERROR";

	private static Preferences prefs;

	private static GlobalKeyboardHook keyboardHook;

	private static List<SettingsListener> settingsListeners;


	public static void initialize()
	{
		initPreferenceData();
		initKeyboardHook();

		settingsListeners = new ArrayList<>();
	}


	/**
	 * Inits the preferences.
	 */
	private static void initPreferenceData()
	{
		prefs = Preferences.userRoot().node(PREFERENCES_PATH);
		prefs.put(PREFS_KEY_VERSION, VersionManager.getVersion());

		// import legacy preferences to new path, delete old path
		boolean initStartup = prefs.getBoolean(PREFS_KEY_INITIAL_STARTUP, true);
		if (initStartup)
		{
			try
			{
				if (Preferences.userRoot().nodeExists(PREFERENCES_PATH_LEGACY))
				{
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					Preferences.userRoot().node(PREFERENCES_PATH_LEGACY).exportSubtree(os);
					String legacyPrefs = new String(os.toByteArray(), "UTF-8");
					legacyPrefs = legacyPrefs.replaceFirst("speedsouls", "soulsspeedruns");

					Preferences.importPreferences(new ByteArrayInputStream(legacyPrefs.getBytes()));

					Preferences.userRoot().node(PREFERENCES_PATH_LEGACY).removeNode();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			prefs.putBoolean(PREFS_KEY_INITIAL_STARTUP, false);
		}
	}


	/**
	 * Inits the global hotkey hook.
	 */
	private static void initKeyboardHook()
	{
		try
		{
			keyboardHook = new GlobalKeyboardHook();
			keyboardHook.setHotkeysEnabled(prefs.getBoolean(PREFS_KEY_SETTING_GLOBAL_HOTKEYS, false));
		}
		catch (NativeHookException e)
		{
			JOptionPane.showMessageDialog(OrganizerManager.getMainWindow(),
					"Error when trying to establish the keyboard hook. Global hotkeys will be disabled.", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			keyboardHook.setHotkeysEnabled(false);
		}
	}


	/**
	 * Returns the keyboard hook.
	 * 
	 * @return the keyboard hook
	 */
	public static GlobalKeyboardHook getKeyboardHook()
	{
		return keyboardHook;
	}


	/**
	 * Saves the properties of this game to the preferences.
	 * 
	 * @param game the game to store
	 */
	public static void storeGameProperties(Game game)
	{
		if (!game.isCustomGame())
		{
			if (game.getDirectory() != null)
				prefs.put(game.getGameID() + PREFS_MODIFIER_GAME_DIR, game.getDirectory().getPath());
			if (game.getSaveFileLocation() != null)
				prefs.put(game.getGameID() + PREFS_MODIFIER_GAME_SAVEFILE, game.getSaveFileLocation().getPath());

			prefs.putInt(game.getGameID() + PREFS_MODIFIER_GAME_INDEX, game.getListIndex());
			return;
		}

		if (game.getDirectory() != null)
			prefs.put(PREFS_PREFIX_CUSTOM_GAME + game.getGameID() + "_" + PREFS_MODIFIER_GAME_DIR, game.getDirectory().getPath());
		if (game.getSaveFileLocation() != null)
			prefs.put(PREFS_PREFIX_CUSTOM_GAME + game.getGameID() + "_" + PREFS_MODIFIER_GAME_SAVEFILE, game.getSaveFileLocation().getPath());

		prefs.put(PREFS_PREFIX_CUSTOM_GAME + game.getGameID() + "_" + PREFS_MODIFIER_GAME_GAMENAME, game.getCaption());
		prefs.put(PREFS_PREFIX_CUSTOM_GAME + game.getGameID() + "_" + PREFS_MODIFIER_GAME_SAVENAME, game.getSaveName());
		prefs.putInt(PREFS_PREFIX_CUSTOM_GAME + game.getGameID() + "_" + PREFS_MODIFIER_GAME_INDEX, game.getListIndex());
	}


	/**
	 * Deletes the given custom game from the preferences.
	 * 
	 * @param game the game to delete
	 */
	public static void deleteGameProperties(Game game)
	{
		if (!game.isCustomGame())
			return;

		String gameID = game.getGameID();

		prefs.remove(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_GAMENAME);
		prefs.remove(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_SAVENAME);
		prefs.remove(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_SAVEFILE);
		prefs.remove(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_DIR);
		prefs.remove(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_INDEX);
	}


	public static List<String> getStoredCustomGameIDs()
	{
		try
		{
			String[] preferences = prefs.keys();
			List<String> gameIDs = new ArrayList<>();

			for (String key : preferences)
			{
				if (key.startsWith(PREFS_PREFIX_CUSTOM_GAME) && key.endsWith(PREFS_MODIFIER_GAME_GAMENAME))
					gameIDs.add(key.substring(PREFS_PREFIX_CUSTOM_GAME.length(), key.lastIndexOf("_")));
			}

			gameIDs.sort(null);

			return gameIDs;
		}
		catch (BackingStoreException e1)
		{
			return null;
		}
	}


	public static String getStoredCustomGameGameName(String gameID)
	{
		return prefs.get(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_GAMENAME, null);
	}


	public static String getStoredCustomGameSaveName(String gameID)
	{
		return prefs.get(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_SAVENAME, null);
	}


	public static String getStoredCustomGameSaveLocation(String gameID)
	{
		return prefs.get(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_SAVEFILE, null);
	}


	public static String getStoredCustomGameProfilesLocation(String gameID)
	{
		return prefs.get(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_DIR, null);
	}


	public static int getStoredCustomGameIndex(String gameID)
	{
		return prefs.getInt(PREFS_PREFIX_CUSTOM_GAME + gameID + "_" + PREFS_MODIFIER_GAME_INDEX, Game.GAMES.size());
	}


	public static String getStoredGameSaveLocation(Game game)
	{
		return prefs.get(game.getGameID() + PREFS_MODIFIER_GAME_SAVEFILE, null);
	}


	public static String getStoredGameProfilesLocation(Game game)
	{
		return prefs.get(game.getGameID() + PREFS_MODIFIER_GAME_DIR, null);
	}


	public static int getStoredGameIndex(Game game)
	{
		return prefs.getInt(game.getGameID() + PREFS_MODIFIER_GAME_INDEX, 0);
	}


	public static String getStoredSelectedGameName()
	{
		return prefs.get(PREFS_KEY_SELECTED_GAME, "");
	}


	public static String getStoredSelectedProfileName()
	{
		return prefs.get(PREFS_KEY_SELECTED_PROFILE, "");
	}


	public static void setStoredSelectedGameName(String name)
	{
		prefs.put(PREFS_KEY_SELECTED_GAME, name);
	}


	public static void setStoredSelectedProfileName(String name)
	{
		prefs.put(PREFS_KEY_SELECTED_PROFILE, name);
	}


	public static String getStoredSelectedSortingCategoryName()
	{
		return prefs.get(PREFS_KEY_SELECTED_SORTING, "");
	}


	public static void setStoredSelectedSortingCategoryName(String name)
	{
		prefs.put(PREFS_KEY_SELECTED_SORTING, name);
	}


	/**
	 * Adds a settings listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSettingsListener(SettingsListener listener)
	{
		if (listener != null)
			settingsListeners.add(listener);
	}


	/**
	 * Stores the prefix of the given theme in the preferences. The corresponding theme will be loaded on restart.
	 * 
	 * @param theme the theme to store
	 */
	public static void setStoredTheme(Theme theme)
	{
		prefs.put(PREFS_KEY_THEME, theme.getPrefix());
	}


	/**
	 * Returns the theme that was stored. If none is stored, returns a default choice based on the user theme preferences.
	 * 
	 * @return the stored theme
	 */
	public static Theme getStoredTheme()
	{
		String prefix = prefs.get(PREFS_KEY_THEME, PREFS_ERROR_ON_RETRIEVE);
		if (prefix != PREFS_ERROR_ON_RETRIEVE)
		{
			for (Theme theme : LafManager.getRegisteredThemes())
			{
				if (theme.getPrefix().equalsIgnoreCase(prefix))
					return theme;
			}
		}

		boolean dark = LafManager.getPreferredThemeStyle().getColorToneRule() == ColorToneRule.DARK;
		Theme theme = dark ? new SoulsSpeedrunsTheme() : new DefaultTheme();

		setStoredTheme(theme);

		return theme;
	}


	/**
	 * Enables/disables global hotkeys.
	 * 
	 * @param flag True to enable, false to disable
	 */
	public static void setGlobalHotkeysEnabled(boolean flag)
	{
		if (areGlobalHotkeysEnabled() == flag)
			return;
		prefs.putBoolean(PREFS_KEY_SETTING_GLOBAL_HOTKEYS, flag);
		keyboardHook.setHotkeysEnabled(flag);
		fireSettingChangedEvent(PREFS_KEY_SETTING_GLOBAL_HOTKEYS);
	}


	/**
	 * Returns whether global hotkeys are enabled.
	 * 
	 * @return whether global hotkeys are enabled
	 */
	public static boolean areGlobalHotkeysEnabled()
	{
		return keyboardHook.areHotkeysEnabled();
	}


	/**
	 * Sets the main window to always be on top or not.
	 * 
	 * @param flag True for the window to always be on top
	 */
	public static void setAlwaysOnTop(boolean flag)
	{
		if (isAlwaysOnTop() == flag)
			return;
		prefs.putBoolean(PREFS_KEY_SETTING_ALWAYS_ON_TOP, flag);
		OrganizerManager.getMainWindow().setAlwaysOnTop(flag);
		fireSettingChangedEvent(PREFS_KEY_SETTING_ALWAYS_ON_TOP);
	}


	/**
	 * Returns whether the window should always be on top.
	 * 
	 * @return whether the window should always be on top
	 */
	public static boolean isAlwaysOnTop()
	{
		return prefs.getBoolean(PREFS_KEY_SETTING_ALWAYS_ON_TOP, false);
	}


	/**
	 * Enables/disables double click to load.
	 * 
	 * @param flag True to enable, false to disable
	 */
	public static void setDoubleClickLoadEnabled(boolean flag)
	{
		if (isDoubleClickLoadEnabled() == flag)
			return;
		prefs.putBoolean(PREFS_KEY_SETTING_DOUBLE_CLICK_LOAD, flag);
		fireSettingChangedEvent(PREFS_KEY_SETTING_DOUBLE_CLICK_LOAD);
	}


	/**
	 * Returns whether double click to load is enabled.
	 * 
	 * @return whether double click to load is enabled
	 */
	public static boolean isDoubleClickLoadEnabled()
	{
		return prefs.getBoolean(PREFS_KEY_SETTING_DOUBLE_CLICK_LOAD, false);
	}


	/**
	 * Enables/disables the check for new releases.
	 * 
	 * @param flag True to enable, false to disable
	 */
	public static void setCheckForUpdatesEnabled(boolean flag)
	{
		if (isCheckForUpdatesEnabled() == flag)
			return;
		prefs.putBoolean(PREFS_KEY_SETTING_CHECK_FOR_UPDATES, flag);
		fireSettingChangedEvent(PREFS_KEY_SETTING_CHECK_FOR_UPDATES);
	}


	/**
	 * Returns whether the application should check for new releases.
	 * 
	 * @return whether to check for new releases
	 */
	public static boolean isCheckForUpdatesEnabled()
	{
		return prefs.getBoolean(PREFS_KEY_SETTING_CHECK_FOR_UPDATES, true);
	}


	/**
	 * Enables/disables compact mode which
	 * 
	 * @param flag True to enable, false to disable
	 */
	public static void setCompactModeEnabled(boolean flag)
	{
		if (isCompactModeEnabled() == flag)
			return;
		prefs.putBoolean(PREFS_KEY_SETTING_COMPACT_MODE, flag);
		fireSettingChangedEvent(PREFS_KEY_SETTING_COMPACT_MODE);
	}


	/**
	 * Returns whether compact mode is enabled, i.e. smaller savestate buttons at the bottom
	 * 
	 * @return whether compact mode is enabled
	 */
	public static boolean isCompactModeEnabled()
	{
		return prefs.getBoolean(PREFS_KEY_SETTING_COMPACT_MODE, false);
	}


	/**
	 * Returns the window size stored in the preferences.
	 * 
	 * @return the window size as a Dimension object
	 */
	public static Dimension getStoredWindowSize()
	{
		int width = prefs.getInt(PREFS_KEY_WIN_WIDTH, -1);
		int height = prefs.getInt(PREFS_KEY_WIN_HEIGHT, -1);

		return new Dimension(width, height);
	}


	/**
	 * Sets the window size variables stored in the preferences.
	 * 
	 * @param size the size as a Dimension object
	 */
	public static void setStoredWindowSize(Dimension size)
	{
		prefs.putInt(PREFS_KEY_WIN_WIDTH, size.width);
		prefs.putInt(PREFS_KEY_WIN_HEIGHT, size.height);
	}


	/**
	 * Returns whether the main window is stored as maximized in the preferences.
	 * 
	 * @return whether the main window is maximized
	 */
	public static int getStoredMaximizedWindowState()
	{
		return prefs.getInt(PREFS_KEY_MAXIMIZED, -1);
	}


	/**
	 * Sets whether the main window should be stored as maximized in the preferences
	 * 
	 * @param state whether it is maximized or not
	 */
	public static void setStoredMaximizedWindowState(int state)
	{
		prefs.putInt(PREFS_KEY_MAXIMIZED, state);
	}


	public static String getStoredHotkeyCode(GlobalHotkey hotkey)
	{
		return prefs.get(hotkey.getPrefsKey(), "");
	}


	/**
	 * Stores the given code for the given hotkey in the preferences.
	 * 
	 * @param hotkey  the hotkey
	 * @param keyCode the new key code
	 */
	public static void setStoredHotkeyCode(GlobalHotkey hotkey, String keyCode)
	{
		prefs.put(hotkey.getPrefsKey(), keyCode);
	}


	/**
	 * Fires a settingChanged event.
	 * 
	 * @param prefsKey the key of the preference value that was changed
	 */
	public static void fireSettingChangedEvent(String prefsKey)
	{
		for (SettingsListener listener : settingsListeners)
		{
			listener.settingChanged(prefsKey);
		}
	}

}
