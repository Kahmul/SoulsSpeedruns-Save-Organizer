package com.speedsouls.organizer.data;


import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jnativehook.NativeHookException;

import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.GlobalHotkey;
import com.speedsouls.organizer.content.Profile;
import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.content.SortingCategory;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;
import com.speedsouls.organizer.listeners.SearchListener;
import com.speedsouls.organizer.listeners.SortingListener;
import com.speedsouls.organizer.main.OrganizerWindow;
import com.speedsouls.organizer.natives.GlobalKeyboardHook;


/**
 * OrganizerManager.
 * <p>
 * Manages storing and retrieving of the used data (e.g. profiles, saves, images). Handles listener events for profiles, saves and
 * games. Offers utility methods.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class OrganizerManager
{

	public static final String VERSION = "1.3.1";
	public static final String WEB_PAGE_URL = "www.speedsouls.com/SpeedSouls_-_Save_Organizer";

	private static final String RESOURCE_PATH = "/com/speedsouls/organizer/resources/";
	private static final String PREFERENCES_PATH = "/com/speedsouls/organizer/prefs";

	private static final String PREFS_KEY_WIN_WIDTH = "WindowWidth";
	private static final String PREFS_KEY_WIN_HEIGHT = "WindowHeight";

	private static final String PREFS_KEY_SELECTED_GAME = "selectedGame";
	private static final String PREFS_KEY_SELECTED_PROFILE = "selectedProfile";
	private static final String PREFS_KEY_SELECTED_SORTING = "selectedSorting";

	private static final String PREFS_KEY_SETTING_ALWAYS_ON_TOP = "alwaysOnTop";
	private static final String PREFS_KEY_SETTING_GLOBAL_HOTKEYS = "globalHotkeys";

	public static final String PREFS_KEY_GLOBAL_HOTKEY_LOAD = "hotkeyLoad";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_READ_ONLY = "hotkeyReadOnly";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_TOGGLE = "hotkeyToggle";

	private static Preferences prefs;

	private static GlobalKeyboardHook keyboardHook;

	public static Image speedsoulsIcon;
	public static Image speedsoulsIconMedium;
	public static Image readOnlyIconMedium;
	public static Image readOnlyIconSmall;
	public static Image settingsIcon;

	private static Map<Game, List<Profile>> profiles;

	private static List<ProfileListener> profileListeners;
	private static List<SaveListener> saveListeners;
	private static List<SearchListener> searchListeners;
	private static List<SortingListener> sortingListeners;

	private static Save selectedSave;

	private static OrganizerWindow mainWindow;

	static
	{
		try
		{
			initialize();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error when trying to initialize the data. Could not start the Save Organizer.",
					"Error occured", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}


	/**
	 * Initializes the data. Required to be called before using any other methods of this class.
	 * 
	 * @throws IOException
	 */
	public static void initialize() throws IOException
	{
		importImages();
		initListeners();
		initPreferenceData();
		initKeyboardHook();
		mapGamesWithProfiles();
	}


	/**
	 * Imports all the images and icons used in the program.
	 * 
	 * @throws IOException
	 */
	private static void importImages() throws IOException
	{
		speedsoulsIcon = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SpeedSoulsIcon.png"));
		speedsoulsIconMedium = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SpeedSoulsIconMedium.png"));
		readOnlyIconSmall = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIconSmall.png"));
		readOnlyIconMedium = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIconMedium.png"));
		settingsIcon = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SettingsIcon.png"));
	}


	/**
	 * Inits the listener lists.
	 */
	private static void initListeners()
	{
		profileListeners = new ArrayList<>();
		saveListeners = new ArrayList<>();
		searchListeners = new ArrayList<>();
		sortingListeners = new ArrayList<>();
	}


	/**
	 * Inits the preferences.
	 */
	private static void initPreferenceData()
	{
		prefs = Preferences.userRoot().node(PREFERENCES_PATH);
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
			JOptionPane.showMessageDialog(mainWindow, "Error when trying to establish the keyboard hook. Global hotkeys will be disabled.",
					"Error occured", JOptionPane.ERROR_MESSAGE);
			keyboardHook.setHotkeysEnabled(false);
		}
	}


	/**
	 * Maps all the existing games with their profiles.
	 */
	private static void mapGamesWithProfiles()
	{
		profiles = new HashMap<>();
		if (prefs == null)
			return;
		Game[] games = Game.values();
		for (Game game : games)
			importProfiles(game);
	}


	/**
	 * Searches for all the profiles of the given game.
	 * 
	 * @param game the game to import the profiles for
	 */
	private static void importProfiles(Game game)
	{
		String gameDirectoryPath = prefs.get(game.getAbbreviation() + "Path", "ERROR");
		if (gameDirectoryPath.equals("ERROR"))
			return;
		File gameDirectory = new File(gameDirectoryPath);
		if (!gameDirectory.exists())
		{
			prefs.remove(game.getAbbreviation() + "Path");
			return;
		}
		game.setDirectory(gameDirectory);
		updateProfiles(game);
	}


	/**
	 * Updates the profile list for this game.
	 * 
	 * @param game the game to update the profiles for.
	 */
	public static void updateProfiles(Game game)
	{
		File[] profileFiles = game.getDirectory().listFiles();
		List<Profile> profileList = new ArrayList<>();
		for (int i = 0; i < profileFiles.length; i++)
		{
			if (!profileFiles[i].isDirectory())
				continue;
			profileList.add(new Profile(profileFiles[i].getName(), game));
		}
		profiles.put(game, profileList);
		fireProfilesUpdatedEvent(game);
	}


	/**
	 * Saves the properties of this game.
	 * 
	 * @param game the game to update
	 */
	public static void saveProperties(Game game)
	{
		prefs.put(game.getAbbreviation() + "Path", game.getDirectory().getPath());
		importProfiles(game);
	}


	/**
	 * Returns the existing profiles for this game.
	 * 
	 * @param game the game to get the profiles for
	 * @return a list of all profiles for the given game
	 */
	public static List<Profile> getProfiles(Game game)
	{
		return profiles.get(game) != null ? profiles.get(game) : new ArrayList<Profile>();
	}


	/**
	 * Adds a profile listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addProfileListener(ProfileListener listener)
	{
		if (listener != null)
			profileListeners.add(listener);
	}


	/**
	 * Adds a save listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSaveListener(SaveListener listener)
	{
		if (listener != null)
			saveListeners.add(listener);
	}


	/**
	 * Adds a search listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSearchListener(SearchListener listener)
	{
		if (listener != null)
			searchListeners.add(listener);
	}


	/**
	 * Adds a sorting listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSortingListener(SortingListener listener)
	{
		if (listener != null)
			sortingListeners.add(listener);
	}


	/**
	 * Switches the currently selected profile.
	 * 
	 * @param profile the profile to switch to
	 */
	public static void switchToProfile(Profile profile)
	{
		prefs.put(PREFS_KEY_SELECTED_PROFILE, profile.getName());
		fireChangedToProfileEvent(profile);
	}


	/**
	 * Switches the currently selected game.
	 * 
	 * @param game the game to switch to
	 */
	public static void switchToGame(Game game)
	{
		prefs.put(PREFS_KEY_SELECTED_GAME, game.getCaption());
		fireChangedToGameEvent(game);
	}


	/**
	 * Sets the currently selected save in the SaveList.
	 * 
	 * @param save the selected save
	 */
	public static void setSelectedSave(Save save)
	{
		selectedSave = save;
		fireSaveSelectedEvent(save);
	}


	/**
	 * Returns the currently selected save in the SaveList.
	 * 
	 * @return the save selected in the SaveList
	 */
	public static Save getSelectedSave()
	{
		return selectedSave;
	}


	/**
	 * Returns the currently selected game.
	 * 
	 * @return the game that is currently selected
	 */
	public static Game getSelectedGame()
	{
		String gameName = prefs.get(PREFS_KEY_SELECTED_GAME, "");
		Game[] games = Game.values();
		for (Game game : games)
		{
			if (game.getCaption().equals(gameName))
			{
				return game;
			}
		}
		return games.length > 0 ? games[0] : null;
	}


	/**
	 * Returns the currently selected profile.
	 * 
	 * @return the profile that is currently selected
	 */
	public static Profile getSelectedProfile()
	{
		String profileName = prefs.get(PREFS_KEY_SELECTED_PROFILE, "");
		Game game = getSelectedGame();
		List<Profile> profiles = getProfiles(game);
		for (Profile profile : profiles)
		{
			if (profile.getName().equals(profileName))
				return profile;
		}
		return profiles.size() > 0 ? profiles.get(0) : new Profile("", game);
	}


	/**
	 * Imports the given directories as profiles into the given game.
	 * 
	 * @param files the files to import as profiles
	 * @param game the game the profiles will be imported into
	 */
	public static void importAsProfiles(File[] files, Game game)
	{
		if (files == null)
			return;
		for (File file : files)
		{
			try
			{
				if (!file.isDirectory())
					continue;
				copyDirectory(file, new File(game.getDirectory() + File.separator + file.getName()));
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(mainWindow, "Error when trying to import the profiles!", "Error occured",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		updateProfiles(game);
	}


	/**
	 * Imports the given file as a save into the given profile.
	 * 
	 * @param save the save to import
	 * @param profile the profile to import the save into
	 */
	public static void importAsSave(File file, Profile profile)
	{
		File dir = profile.getDirectory();
		if (!dir.exists())
			return;
		if (selectedSave != null)
			dir = selectedSave.getFile();
		if (!dir.isDirectory())
			dir = dir.getParentFile();
		File newFile = new File(dir.getPath() + File.separator + file.getName());
		for (int i = 0; newFile.exists(); i++)
		{
			newFile = new File(dir.getPath() + File.separator + file.getName() + "_" + i);
		}
		try
		{
			Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			fireAddedToProfileEvent(new Save(newFile), profile);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(mainWindow, "Error when trying to import the savefile!", "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Creates a folder with the given name in the given profile.
	 * 
	 * @param name the name of the folder
	 * @param profile the profile that the folder should be added to
	 */
	public static void createFolder(String name, Profile profile)
	{
		File dir = profile.getDirectory();
		if (!dir.exists())
			return;
		if (selectedSave != null)
			dir = selectedSave.getFile();
		if (!dir.isDirectory())
			dir = dir.getParentFile();
		File folder = new File(dir.getPath() + File.separator + name);
		if (folder.exists())
		{
			JOptionPane.showMessageDialog(mainWindow, "This folder already exists!", "Error occured", JOptionPane.ERROR_MESSAGE);
			return;
		}
		folder.mkdirs();
		fireAddedToProfileEvent(new Save(folder), profile);
	}


	/**
	 * Removes the given save from the given profile.
	 * 
	 * @param save the save to remove
	 * @param profile the profile to remove the save from
	 */
	public static void removeSave(Save save, Profile profile)
	{
		fireRemovedFromProfileEvent(save, profile);
		deleteDirectory(save.getFile());
	}


	/**
	 * Renames the given save to the given name.
	 * 
	 * @param save the save to rename
	 * @param newName the new name of the save
	 */
	public static void renameSave(Save save, String newName)
	{
		save.getFile().renameTo(new File(save.getFile().getParentFile() + File.separator + newName));
		fireSaveRenamedEvent(save, newName);
	}


	/**
	 * Loads the given save and overwrites the current gamefile.
	 * 
	 * @param save the save to load
	 */
	public static void loadSave(Save save)
	{
		if (save == null || save.isDirectory())
			return;
		fireSaveLoadStartedEvent(save);
		Game game = getSelectedGame();
		File gameFile = game.getSaveFile();
		File saveFile = save.getFile();
		boolean canWriteSaveFile = saveFile.canWrite();
		try
		{
			gameFile.setWritable(true);
			saveFile.setWritable(true);
			Files.copy(saveFile.toPath(), gameFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, "Error when trying to load the savefile!", "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}
		gameFile.setWritable(canWriteSaveFile);
		saveFile.setWritable(canWriteSaveFile);
		fireSaveLoadFinishedEvent(save);
	}


	/**
	 * Opens the SpeedSouls webpage for the Save Organizer in the default browser.
	 */
	public static void openWebPage()
	{
		try
		{
			Desktop.getDesktop().browse(new URI(WEB_PAGE_URL));
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Error occured", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Enables/disables global hotkeys.
	 * 
	 * @param flag True to enable, false to disable
	 */
	public static void setGlobalHotkeysEnabled(boolean flag)
	{
		prefs.putBoolean(PREFS_KEY_SETTING_GLOBAL_HOTKEYS, flag);
		keyboardHook.setHotkeysEnabled(flag);
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
		prefs.putBoolean(PREFS_KEY_SETTING_ALWAYS_ON_TOP, flag);
		mainWindow.setAlwaysOnTop(flag);
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
	 * Switches the gamefile of the currently selected game between read-only and writeable.
	 */
	public static void switchCurrentGameFileWritableState()
	{
		File gameFile = getSelectedGame().getSaveFile();
		gameFile.setWritable(!gameFile.canWrite());
		fireGameFileWritableStateChangedEvent(gameFile.canWrite());
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


	public static String getStoredHotkeyCode(GlobalHotkey hotkey)
	{
		return prefs.get(hotkey.getPrefsKey(), "");
	}


	/**
	 * Stores the given code for the given hotkey in the preferences.
	 * 
	 * @param hotkey the hotkey
	 * @param keyCode the new key code
	 */
	public static void setStoredHotkeyCode(GlobalHotkey hotkey, String keyCode)
	{
		prefs.put(hotkey.getPrefsKey(), keyCode);
	}


	public static SortingCategory getSelectedSortingCategory()
	{
		String caption = prefs.get(PREFS_KEY_SELECTED_SORTING, "ERROR");
		for (SortingCategory category : SortingCategory.values())
		{
			if (category.getCaption().equals(caption))
				return category;
		}
		return SortingCategory.ALPHABET;
	}


	public static void setSelectedSortingCategory(SortingCategory category)
	{
		prefs.put(PREFS_KEY_SELECTED_SORTING, category.getCaption());
		fireSortingChangedEvent(category);
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
	 * Sets the main window for the manager.
	 * 
	 * @param window the main window
	 */
	public static void setMainWindow(OrganizerWindow window)
	{
		mainWindow = window;
	}


	/**
	 * @return the main window
	 */
	public static OrganizerWindow getMainWindow()
	{
		return mainWindow;
	}


	/**
	 * Fires a searchRequested event.
	 * 
	 * @param input the search input
	 */
	public static void fireSearchRequestedEvent(String input)
	{
		for (SearchListener listener : searchListeners)
		{
			listener.searchRequested(input);
		}
	}


	/**
	 * Fires a sortingChanged event.
	 * 
	 * @param category the category that was changed to
	 */
	public static void fireSortingChangedEvent(SortingCategory category)
	{
		for (SortingListener listener : sortingListeners)
		{
			listener.sortingChanged(category);
		}
	}


	/**
	 * Fires a profilesUpdated event.
	 * 
	 * @param game the game that the profiles were updated for
	 */
	public static void fireProfilesUpdatedEvent(Game game)
	{
		for (ProfileListener listener : profileListeners)
		{
			listener.profilesUpdated(game);
		}
	}


	/**
	 * Fires an addedToProfile event.
	 * 
	 * @param save the save that was added
	 * @param profile the profile the save was added to
	 */
	public static void fireAddedToProfileEvent(Save save, Profile profile)
	{
		for (ProfileListener profileListener : profileListeners)
		{
			profileListener.addedToProfile(save, profile);
		}
	}


	/**
	 * Fires a removedFromProfile event.
	 * 
	 * @param save the save that was removed
	 * @param profile the profile the save was removed from
	 */
	public static void fireRemovedFromProfileEvent(Save save, Profile profile)
	{
		for (ProfileListener profileListener : profileListeners)
		{
			profileListener.removedFromProfile(save, profile);
		}
	}


	/**
	 * Fires a changedToProfile event.
	 * 
	 * @param profile the profile that was changed to
	 */
	public static void fireChangedToProfileEvent(Profile profile)
	{
		for (ProfileListener listener : profileListeners)
		{
			listener.changedToProfile(profile);
		}
	}


	/**
	 * Fires a changedToGame event.
	 * 
	 * @param game the game that was changed to
	 */
	public static void fireChangedToGameEvent(Game game)
	{
		for (ProfileListener listener : profileListeners)
		{
			listener.changedToGame(game);
		}
	}


	/**
	 * Fires a saveSelected event.
	 * 
	 * @param save the save that was selected
	 */
	public static void fireSaveSelectedEvent(Save save)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.saveSelected(save);
		}
	}


	/**
	 * Fires a saveRenamed event.
	 * 
	 * @param save the save that was renamed
	 * @param newName the new name given to the save
	 */
	public static void fireSaveRenamedEvent(Save save, String newName)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.saveRenamed(save, newName);
		}
	}


	/**
	 * Fires a saveLoadStarted event.
	 * 
	 * @param save the save that is loaded
	 */
	public static void fireSaveLoadStartedEvent(Save save)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.saveLoadStarted(save);
		}
	}


	/**
	 * Fires a saveLoadFinished event.
	 * 
	 * @param save the save that was loaded
	 */
	public static void fireSaveLoadFinishedEvent(Save save)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.saveLoadFinished(save);
		}
	}


	/**
	 * Fires a gameFileWritableStateChanged event.
	 * 
	 * @param save the save that was loaded
	 */
	public static void fireGameFileWritableStateChangedEvent(boolean writeable)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.gameFileWritableStateChanged(writeable);
		}
	}


	/**
	 * Checks whether the given file contains the given string in its name, and if not checks its subcontents for such a file if
	 * the given file is a directory.
	 * 
	 * @param directory the file/directory to check
	 * @param name the name to check for
	 * @return whether a file containing the name was found or not
	 */
	public static boolean containsFileWithName(File directory, String name)
	{
		if (directory.exists())
		{
			if (directory.getName().toLowerCase().contains(name.toLowerCase()))
				return true;
			File[] files = directory.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].getName().toLowerCase().contains(name.toLowerCase()))
						return true;
					else if (files[i].isDirectory())
						if (containsFileWithName(files[i], name))
							return true;
				}
			}
		}
		return false;
	}


	/**
	 * Copies the source directory and its contents into the destination directory.
	 * 
	 * @param src the source directory
	 * @param dest the destination directory
	 * @throws IOException
	 */
	public static void copyDirectory(File src, File dest) throws IOException
	{
		if (src.isDirectory())
		{
			if (!dest.exists())
				dest.mkdir();
			File[] files = src.listFiles();
			for (File file : files)
			{
				File srcFile = new File(src.getPath(), file.getName());
				File destFile = new File(dest.getPath(), file.getName());
				copyDirectory(srcFile, destFile);
			}
			return;
		}
		Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}


	/**
	 * Deletes the given directory and all of its sub folders, or simply deletes the given file if its not a directory.
	 * 
	 * @param directory the directory to delete
	 * @return whether the deletion was successful or not
	 */
	public static boolean deleteDirectory(File directory)
	{
		if (directory.exists())
		{
			File[] files = directory.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						deleteDirectory(files[i]);
					}
					else
					{
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}


	/**
	 * Checks the given string for illegal characters.
	 * 
	 * @param toExamine the string to examine
	 * @return True if the string contains illegal characters. False otherwise.
	 */
	public static boolean containsIllegals(String toExamine)
	{
		String[] arr = toExamine.split("[~#@*+%{}<>\\[\\]|\"\\_^]", 2);
		return arr.length > 1;
	}

}
