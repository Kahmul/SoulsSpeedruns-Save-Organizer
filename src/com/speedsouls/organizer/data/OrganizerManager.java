package com.speedsouls.organizer.data;


import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jnativehook.NativeHookException;
import org.json.JSONObject;

import com.speedsouls.organizer.games.Game;
import com.speedsouls.organizer.hotkeys.GlobalHotkey;
import com.speedsouls.organizer.hotkeys.GlobalKeyboardHook;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;
import com.speedsouls.organizer.listeners.SearchListener;
import com.speedsouls.organizer.listeners.SortingListener;
import com.speedsouls.organizer.main.OrganizerWindow;
import com.speedsouls.organizer.messages.AbstractMessage;
import com.speedsouls.organizer.profileconfig.Profile;
import com.speedsouls.organizer.savelist.Folder;
import com.speedsouls.organizer.savelist.Save;
import com.speedsouls.organizer.savelist.SaveListEntry;
import com.speedsouls.organizer.savelist.SortingCategory;

import jiconfont.icons.Elusive;
import jiconfont.icons.Entypo;
import jiconfont.icons.FontAwesome;
import jiconfont.icons.Iconic;
import jiconfont.icons.Typicons;
import jiconfont.swing.IconFontSwing;

import static com.speedsouls.organizer.messages.AbstractMessage.SUCCESSFUL_IMPORT;


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

	public static final String VERSION = "1.4";

	/**
	 * Constants defining various URLs.
	 */
	public static final String WEB_PAGE_URL = "www.speedsouls.com/SpeedSouls_-_Save_Organizer";
	public static final String GITHUB_REPO_URL = "www.github.com/Kahmul/SpeedSouls-Save-Organizer";
	public static final String TWITTER_URL = "www.twitter.com/Kahmul78";
	public static final String LATEST_RELEASE_JSON_URL = "https://api.github.com/repos/Kahmul/SpeedSouls-Save-Organizer/releases/latest";
	public static final String LATEST_RELEASE_URL = "https://github.com/Kahmul/SpeedSouls-Save-Organizer/releases";

	/**
	 * Constants for paths to preferences and resources.
	 */
	private static final String RESOURCE_PATH = "/com/speedsouls/organizer/resources/";
	private static final String PREFERENCES_PATH = "/com/speedsouls/organizer/prefs";

	/**
	 * Constants for the keys used to access preferences.
	 */
	private static final String PREFS_KEY_INITIAL_STARTUP = "initStartup";
	private static final String PREFS_KEY_VERSION = "Version";

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

	public static final String PREFS_MODIFIER_GAME_DIR = "Path";
	public static final String PREFS_MODIFIER_GAME_SAVEFILE = "Savefile";

	public static final String PREFS_ERROR_ON_RETRIEVE = "ERROR";

	public static final String ILLEGAL_CHARACTERS = "~, @, *, {, }, <, >, [, ], |, “, ”, \\, /, ^";
	private static final String ILLEGAL_CHARACTERS_REGEX = "[~#@*{}<>\\[\\]|\"\\^\\\\\\/]";

	private static Preferences prefs;

	private static GlobalKeyboardHook keyboardHook;

	public static Image speedsoulsIcon;
	public static Image speedsoulsIconMedium;
	public static Image readOnlyIconMedium;
	public static Image readOnlyIconSmall;
	public static Image settingsIcon;

	private static List<ProfileListener> profileListeners;
	private static List<SaveListener> saveListeners;
	private static List<SearchListener> searchListeners;
	private static List<SortingListener> sortingListeners;

	private static SaveListEntry selectedEntry;

	private static OrganizerWindow mainWindow;

	private static boolean isReady;

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
	private static void initialize() throws IOException
	{
		importImages();
		initListeners();
		initPreferenceData();
		initKeyboardHook();
		mapGamesWithProfiles();

		isReady = true;
	}


	/**
	 * Imports all the images and icons used in the program.
	 * 
	 * @throws IOException
	 */
	private static void importImages() throws IOException
	{
		speedsoulsIcon = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SpeedSoulsFlameSmall.png"));
		speedsoulsIconMedium = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SpeedSoulsFlameSmall.png"));
		readOnlyIconSmall = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIconSmall.png"));
		readOnlyIconMedium = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIconMedium.png"));
		settingsIcon = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SettingsIcon.png"));

		IconFontSwing.register(FontAwesome.getIconFont());
		IconFontSwing.register(Elusive.getIconFont());
		IconFontSwing.register(Entypo.getIconFont());
		IconFontSwing.register(Iconic.getIconFont());
		IconFontSwing.register(Typicons.getIconFont());
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
		prefs.put(PREFS_KEY_VERSION, VERSION);

		// reset global hotkeys if this is the first time the organizer is started to avoid incompatibility with older versions
		boolean initStartup = prefs.getBoolean(PREFS_KEY_INITIAL_STARTUP, true);
		if (initStartup)
		{
			prefs.remove(PREFS_KEY_SETTING_GLOBAL_HOTKEYS);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_LOAD);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_READ_ONLY);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_TOGGLE);

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
		String gameDirectoryPath = prefs.get(game.getAbbreviation() + PREFS_MODIFIER_GAME_DIR, PREFS_ERROR_ON_RETRIEVE);
		if (PREFS_ERROR_ON_RETRIEVE.equals(gameDirectoryPath))
			return;
		File gameDirectory = new File(gameDirectoryPath);
		if (!gameDirectory.exists())
		{
			prefs.remove(game.getAbbreviation() + PREFS_MODIFIER_GAME_DIR);
			return;
		}
		//also sets profiles
		game.setDirectory(gameDirectory);
		String saveLocationPath = prefs.get(game.getAbbreviation() + PREFS_MODIFIER_GAME_SAVEFILE, PREFS_ERROR_ON_RETRIEVE);
		if (!PREFS_ERROR_ON_RETRIEVE.equals(saveLocationPath))
		{
			game.setSaveFileLocation(new File(saveLocationPath));
			return;
		}
		// If the user used an old version, then a savefile location might not have been explicitly specified.
		// In that case, use the profile directory + saveName as the savefile location
		File defaultSaveFileLocation = new File(gameDirectoryPath + File.separator + game.getSaveName());
		if (defaultSaveFileLocation.exists())
			game.setSaveFileLocation(defaultSaveFileLocation);
	}


	public static boolean isApplicationReady()
	{
		return isReady;
	}


	/**
	 * Refreshes all profiles and games.
	 */
	public static void refreshProfiles()
	{
		mapGamesWithProfiles();
	}


	/**
	 * Saves the properties of this game to the preferences.
	 * 
	 * @param game the game to update
	 */
	public static void saveProperties(Game game)
	{
		prefs.put(game.getAbbreviation() + PREFS_MODIFIER_GAME_DIR, game.getDirectory().getPath());
		prefs.put(game.getAbbreviation() + PREFS_MODIFIER_GAME_SAVEFILE, game.getSaveFileLocation().getPath());
		importProfiles(game);
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
		profile.refresh();

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
	 * Sets the currently selected entry in the SaveList.
	 * 
	 * @param entry the selected entry
	 */
	public static void setSelectedEntry(SaveListEntry entry)
	{
		selectedEntry = entry;
		fireEntrySelectedEvent(entry);
	}


	/**
	 * Returns the currently selected entry in the SaveList.
	 * 
	 * @return the entry selected in the SaveList
	 */
	public static SaveListEntry getSelectedEntry()
	{
		return selectedEntry;
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
		List<Profile> profiles = game.getProfiles();
		for (Profile profile : profiles)
		{
			if (profile.getName().equals(profileName))
				return profile;
		}
		// if a profile with the saved name doesn't exist, return either the first existing one, or an empty one.
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
	}


	/**
	 * Imports a new savefile. If a parent is given, it will be imported into that folder. Otherwise the parent will be determined
	 * based on the selection in the save list.
	 * 
	 * @param parentFolder the folder to import the savefile into
	 * @return the imported save
	 */
	public static Save importSaveFile(Folder parentFolder)
	{
		SaveListEntry parent = getSelectedEntry();
		if (parent instanceof Save)
			parent = parent.getParent();
		if (parentFolder != null)
			parent = parentFolder;
		if (parent == null)
			parent = getSelectedProfile().getRoot();
		File saveFile = createFileForNewSave((Folder) parent);
		if (saveFile == null)
			return null;
		Save newSave = new Save((Folder) parent, saveFile);
		parent.addChild(newSave);
		AbstractMessage.display(SUCCESSFUL_IMPORT);
		fireEntryCreatedEvent(newSave);
		return newSave;
	}


	/**
	 * Imports a new savefile and replaces an existing one in the list.
	 * 
	 * @param saveToReplace the save to be replaced by the imported one
	 */
	public static void importAndReplaceSaveFile(Save saveToReplace)
	{
		Folder parent = saveToReplace.getParent();
		String name = saveToReplace.getName();
		saveToReplace.delete();
		File saveFile = createFileForNewSave((Folder) parent);
		if (saveFile == null)
			return;
		Save newSave = new Save(parent, saveFile);
		newSave.rename(name);
		parent.addChild(newSave);
		fireEntryCreatedEvent(newSave);
		AbstractMessage.display(AbstractMessage.SUCCESSFUL_REPLACE);
	}


	public static void createFolder(String name)
	{
		SaveListEntry parent = getSelectedEntry();
		if (parent == null)
			parent = getSelectedProfile().getRoot();
		if (parent instanceof Save)
			parent = parent.getParent();
		File dir = new File(parent.getFile().getPath() + File.separator + name);
		if (dir.exists())
		{
			JOptionPane.showMessageDialog(mainWindow, "This folder already exists!", "Error occured", JOptionPane.ERROR_MESSAGE);
			return;
		}
		dir.mkdirs();
		Folder newFolder = new Folder((Folder) parent, dir);
		parent.addChild(newFolder);
		fireEntryCreatedEvent(newFolder);
	}


	/**
	 * Creates the File object and the actual file in the file system for a new save in the given parent folder.
	 * 
	 * @param parent the parent of the new save
	 * @return the file object
	 */
	private static File createFileForNewSave(Folder parent)
	{
		if (getSelectedGame().getSaveFileLocation() == null)
		{
			JOptionPane.showMessageDialog(mainWindow,
					"To import a savefile you need to set the savefile location in the profile configuration settings!", "Error occured",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		String parentPath = parent != null ? parent.getFile().getPath() : getSelectedProfile().getRoot().getFile().getPath();
		String name = getSelectedGame().getSaveName();
		File newFile = new File(parentPath + File.separator + name);
		for (int i = 0; newFile.exists(); i++)
			newFile = new File(parentPath + File.separator + name + "_" + i);
		try
		{
			Files.copy(getSelectedGame().getSaveFileLocation().toPath(), newFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(mainWindow, "Error when trying to import the savefile!", "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}
		return newFile;
	}


	/**
	 * Loads the given save and overwrites the current gamefile.
	 * 
	 * @param save the save to load
	 */
	public static void loadSave(Save save)
	{
		if (save == null)
			return;
		fireSaveLoadStartedEvent(save);
		Game game = getSelectedGame();
		File gameFile = game.getSaveFileLocation();
		File saveFile = save.getFile();
		boolean canWriteSaveFile = saveFile.canWrite();
		try
		{
			gameFile.setWritable(true);
			saveFile.setWritable(true);
			Files.copy(saveFile.toPath(), gameFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			AbstractMessage.display(AbstractMessage.SUCCESSFUL_LOAD);
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
	 * Opens the GitHub latest release page.
	 */
	public static void openLatestReleasePage()
	{
		try
		{
			Desktop.getDesktop().browse(new URI(LATEST_RELEASE_URL));
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
		File gameFile = getSelectedGame().getSaveFileLocation();
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
	 * Fires a profileDeleted event.
	 * 
	 * @param profile the deleted profile
	 */
	public static void fireProfileDeletedEvent(Profile profile)
	{
		for (ProfileListener listener : profileListeners)
		{
			listener.profileDeleted(profile);
		}
	}


	/**
	 * Fires a profileCreated event.
	 * 
	 * @param profile the created profile
	 */
	public static void fireProfileCreatedEvent(Profile profile)
	{
		for (ProfileListener listener : profileListeners)
		{
			listener.profileCreated(profile);
		}
	}


	/**
	 * Fires a profileDirectoryChanged event.
	 */
	public static void fireProfileDirectoryChangedEvent(Game game)
	{
		for (ProfileListener listener : profileListeners)
		{
			listener.profileDirectoryChanged(game);
		}
	}


	/**
	 * Fires an entryCreated event.
	 * 
	 * @param entry the entry that was created
	 */
	public static void fireEntryCreatedEvent(SaveListEntry entry)
	{
		for (SaveListener saveListener : saveListeners)
		{
			saveListener.entryCreated(entry);
		}
	}


	/**
	 * Fires an entryRenamed event.
	 * 
	 * @param entry the entry that was renamed
	 */
	public static void fireEntryRenamedEvent(SaveListEntry entry)
	{
		for (SaveListener saveListener : saveListeners)
		{
			saveListener.entryRenamed(entry);
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
	 * Fires an entrySelected event.
	 * 
	 * @param entry the entry that was selected
	 */
	public static void fireEntrySelectedEvent(SaveListEntry entry)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.entrySelected(entry);
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
	 * Fires a fireGameFileWritableStateChangedEvent event.
	 * 
	 * @param writeable is this file writable
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
	 * @return true if the string contains illegal characters. False otherwise.
	 */
	public static boolean containsIllegals(String toExamine)
	{
		String[] arr = toExamine.split(ILLEGAL_CHARACTERS_REGEX, 2);
		return arr.length > 1;
	}


	/**
	 * Checks whether the local Save Organizer version is outdated compared to the latest GitHub release.
	 * 
	 * @return whether the local version is outdated
	 */
	public static boolean isVersionOutdated()
	{
		String[] vals1 = VERSION.split("\\.");
		String[] vals2 = getLatestReleaseVersion().split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
		{
			i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length)
		{
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff) == -1;
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return Integer.signum(vals1.length - vals2.length) == -1;
	}


	/**
	 * Checks the latest release version on GitHub and returns it.
	 * 
	 * @return the latest release version on GitHub
	 */
	public static String getLatestReleaseVersion()
	{
		JSONObject latestReleaseJSON = getLatestReleaseJSON();
		if (latestReleaseJSON != null)
			return latestReleaseJSON.getString("tag_name").substring(2);
		return "0.0";
	}


	/**
	 * Retrieves the description of the latest release from GitHub.
	 * 
	 * @return the latest release description
	 */
	public static String getLatestReleaseDescription()
	{
		JSONObject latestReleaseJSON = getLatestReleaseJSON();
		if (latestReleaseJSON != null)
			return latestReleaseJSON.getString("body");
		return "";
	}


	/**
	 * Builds the download URL based on the latest release version.
	 * 
	 * @return the download URL for the latest release
	 */
	public static String getLatestReleaseDownloadURL()
	{
		String latestVersion = getLatestReleaseVersion();
		return "https://github.com/Kahmul/SpeedSouls-Save-Organizer/releases/download/v." + latestVersion + "SpeedSouls.-.Save.Organizer."
				+ latestVersion + ".zip";
	}


	/**
	 * Creates a JSONObject of the latest release on GitHub.
	 * 
	 * @return the JSONObject of the latest release
	 */
	private static JSONObject getLatestReleaseJSON()
	{
		try (InputStream is = new URL(LATEST_RELEASE_JSON_URL).openStream())
		{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		}
		catch (Exception e)
		{
		}
		return null;
	}


	/**
	 * Reads all the input from a Reader and returns it in a single String.
	 * 
	 * @param rd the reader
	 * @return the input
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1)
		{
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * when we copy a file we need to check if it exists.
	 * If it does we will append a "(i)" to the file name with
	 * i being the number of times that file has appeared in the directory
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFile(File src,Folder dest) throws IOException {
		int count = 1;
		//create temp file to not lose contents of the source
		File temp = src;
		File destFile = dest.getFile();
		String originalName = src.getAbsolutePath();
		while (exists(temp, destFile) && count < 50) {
			temp = new File(createNewFilePathString(originalName, count, temp.isDirectory()));
			count++;

		}
		if (count >= 50) { //mainly this is to account for an edge case most likely this would never happen
			JOptionPane.showMessageDialog(null, "One of the file(s) you are attempting to copy already exists too many times", "Error occurred", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (src.isDirectory()) {
			copyDirectory(src, new File(destFile.getPath() + File.separator + temp.getName()));

		} else {
			Files.copy(src.toPath(), new File(destFile.getPath() + File.separator + temp.getName()).toPath());
			Save save = new Save(dest,new File(destFile.getPath() + File.separator + temp.getName()));
			dest.addChild(save);
			fireEntryCreatedEvent(save);
		}
	}

	private static String createNewFilePathString(String s, int count,boolean isDirectory){
		if(isDirectory){
			return s + "("+count+")";
		}
		int i = s.lastIndexOf(".");
		String[] a =  {s.substring(0, i), s.substring(i)};
		return a[0]+"("+ count +")"+a[1];

	}


	private static boolean exists(File file1,File file2){
		String path = file2.getPath() + File.separator + file1.getName();
		return new File(path).exists();
	}

}
