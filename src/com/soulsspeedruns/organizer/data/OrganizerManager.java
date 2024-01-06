package com.soulsspeedruns.organizer.data;


import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jnativehook.NativeHookException;
import org.json.JSONObject;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.github.weisj.darklaf.theme.Theme;
import com.github.weisj.darklaf.theme.spec.ColorToneRule;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.hotkeys.GlobalHotkey;
import com.soulsspeedruns.organizer.hotkeys.GlobalKeyboardHook;
import com.soulsspeedruns.organizer.listeners.NavigationListener;
import com.soulsspeedruns.organizer.listeners.ProfileListener;
import com.soulsspeedruns.organizer.listeners.SaveListener;
import com.soulsspeedruns.organizer.listeners.SearchListener;
import com.soulsspeedruns.organizer.listeners.SettingsListener;
import com.soulsspeedruns.organizer.listeners.SortingListener;
import com.soulsspeedruns.organizer.main.OrganizerWindow;
import com.soulsspeedruns.organizer.mainconfig.SortingCategory;
import com.soulsspeedruns.organizer.messages.AbstractMessage;
import com.soulsspeedruns.organizer.profileconfig.Profile;
import com.soulsspeedruns.organizer.savelist.Folder;
import com.soulsspeedruns.organizer.savelist.Save;
import com.soulsspeedruns.organizer.savelist.SaveListEntry;
import com.soulsspeedruns.organizer.theme.DefaultTheme;
import com.soulsspeedruns.organizer.theme.GlobalThemeAdjustmentTask;
import com.soulsspeedruns.organizer.theme.GlobalThemeInitTask;
import com.soulsspeedruns.organizer.theme.SoulsSpeedrunsTheme;

import jiconfont.icons.Elusive;
import jiconfont.icons.FontAwesome;
import jiconfont.icons.Iconic;
import jiconfont.swing.IconFontSwing;


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

	public static final String VERSION = "1.5.1";

	/**
	 * Constants defining various URLs.
	 */
	public static final String WEB_PAGE_URL = "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer";
	public static final String GITHUB_REPO_URL = "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer";
	public static final String TWITTER_URL = "https://twitter.com/Kahmul78";
	public static final String LATEST_RELEASE_JSON_URL = "https://api.github.com/repos/Kahmul/SoulsSpeedruns-Save-Organizer/releases/latest";
	public static final String LATEST_RELEASE_URL = "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/releases/latest";

	/**
	 * Constants for paths to preferences and resources.
	 */
	private static final String RESOURCE_PATH = "/com/soulsspeedruns/organizer/resources/";
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

	public static final String PREFS_KEY_SETTING_ALWAYS_ON_TOP = "alwaysOnTop";
	public static final String PREFS_KEY_SETTING_GLOBAL_HOTKEYS = "globalHotkeys";
	public static final String PREFS_KEY_SETTING_DOUBLE_CLICK_LOAD = "doubleClickLoad";
	public static final String PREFS_KEY_SETTING_CHECK_FOR_UPDATES = "checkForUpdates";
	public static final String PREFS_KEY_SETTING_COMPACT_MODE = "compactMode";

	public static final String PREFS_KEY_GLOBAL_HOTKEY_LOAD = "hotkeyLoad";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_READ_ONLY = "hotkeyReadOnly";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_IMPORT_SAVE = "hotkeyImportSave";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_PREV_SAVE = "hotkeyPrevSave";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_NEXT_SAVE = "hotkeyNextSave";
	public static final String PREFS_KEY_GLOBAL_HOTKEY_TOGGLE = "hotkeyToggle";

	public static final String PREFS_MODIFIER_GAME_DIR = "Path";
	public static final String PREFS_MODIFIER_GAME_SAVEFILE = "Savefile";

	public static final String PREFS_ERROR_ON_RETRIEVE = "ERROR";

	public static final String ILLEGAL_CHARACTERS = "~, @, *, {, }, <, >, [, ], |, \u201C, \u201D, \\, /, ^";
	private static final String ILLEGAL_CHARACTERS_REGEX = "[~#@*{}<>\\[\\]|\"\\^\\\\\\/]";

	private static Preferences prefs;

	private static GlobalKeyboardHook keyboardHook;
	
	private static String latestReleaseVersion;

	public static Image soulsspeedrunsIcon;
	public static Image soulsspeedrunsIconSmall;
	public static Image soulsspeedrunsIconMedium;
	public static ImageIcon readOnlyIcon14;
	public static ImageIcon readOnlyIcon22;
	public static ImageIcon writableIcon22;
	public static ImageIcon readOnlyIcon22Hover;
	public static ImageIcon writableIcon22Hover;
	public static ImageIcon discordIcon;
	public static ImageIcon frankerZIcon;
	public static Image settingsIcon;

	private static List<ProfileListener> profileListeners;
	private static List<SaveListener> saveListeners;
	private static List<SearchListener> searchListeners;
	private static List<SortingListener> sortingListeners;
	private static List<NavigationListener> navigationListeners;
	private static List<SettingsListener> settingsListeners;

	private static SaveListEntry selectedEntry;

	private static OrganizerWindow mainWindow;

	private static boolean isReady;
	
//	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);

	static
	{
		try
		{
			initialize();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error when trying to initialize the organizer. Could not start the Save Organizer.",
					"Error occurred", JOptionPane.ERROR_MESSAGE);
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
		initLookAndFeel();
		mapGamesWithProfiles();
		
//		setAppUserModelID();

		isReady = true;
	}
	
//	/**
//	 * Sets the AppUserModelID. Needed to be able to properly pin the .exe to the taskbar.
//	 */
//	private static void setAppUserModelID()
//	{
//		Native.register("shell32");
//		
//		WString appID = new WString("com.soulsspeedruns.saveorganizer");
//		SetCurrentProcessExplicitAppUserModelID(appID);
//	}


	/**
	 * Imports all the images and icons used in the program.
	 * 
	 * @throws IOException
	 */
	private static void importImages() throws IOException
	{
		soulsspeedrunsIcon = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SoulsSpeedrunsLogo32.png"));
		soulsspeedrunsIconSmall = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SoulsSpeedrunsLogo16.png"));
		soulsspeedrunsIconMedium = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SoulsSpeedrunsLogo100.png"));
		settingsIcon = ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "SettingsIcon.png"));
		readOnlyIcon14 = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIcon14.png")));
		readOnlyIcon22 = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIcon22.png")));
		writableIcon22 = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "WritableIcon22.png")));
		readOnlyIcon22Hover = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "ReadOnlyIcon22Hover.png")));
		writableIcon22Hover = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "WritableIcon22Hover.png")));
		discordIcon = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "DiscordLogo.png")));
		frankerZIcon = new ImageIcon(ImageIO.read(OrganizerManager.class.getResourceAsStream(RESOURCE_PATH + "FrankerZ.png")));

		IconFontSwing.register(FontAwesome.getIconFont());
//		IconFontSwing.register(FontAwesomeBrands.getIconFont());
//		IconFontSwing.register(FontAwesomeSolid.getIconFont());
		IconFontSwing.register(Elusive.getIconFont());
//		IconFontSwing.register(Entypo.getIconFont());
		IconFontSwing.register(Iconic.getIconFont());
//		IconFontSwing.register(Typicons.getIconFont());
		
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
		navigationListeners = new ArrayList<>();
		settingsListeners = new ArrayList<>();
	}


	/**
	 * Inits the preferences.
	 */
	private static void initPreferenceData()
	{
		prefs = Preferences.userRoot().node(PREFERENCES_PATH);
		prefs.put(PREFS_KEY_VERSION, VERSION);

		// reset global hotkeys if this is the first time the organizer is started to avoid incompatibility with older versions
		// also import legacy preferences to new path, delete old path
		boolean initStartup = prefs.getBoolean(PREFS_KEY_INITIAL_STARTUP, true);
		if (initStartup)
		{
			prefs.remove(PREFS_KEY_SETTING_GLOBAL_HOTKEYS);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_LOAD);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_READ_ONLY);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_IMPORT_SAVE);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_PREV_SAVE);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_NEXT_SAVE);
			prefs.remove(PREFS_KEY_GLOBAL_HOTKEY_TOGGLE);
			
			try {
				if(Preferences.userRoot().nodeExists(PREFERENCES_PATH_LEGACY))
				{
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					Preferences.userRoot().node(PREFERENCES_PATH_LEGACY).exportSubtree(os);
					String legacyPrefs = new String(os.toByteArray(), "UTF-8");
					legacyPrefs = legacyPrefs.replaceFirst("speedsouls", "soulsspeedruns");
					
					Preferences.importPreferences(new ByteArrayInputStream(legacyPrefs.getBytes()));
					
					Preferences.userRoot().node(PREFERENCES_PATH_LEGACY).removeNode();
				}
			} catch (Exception e) {
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
			JOptionPane.showMessageDialog(mainWindow, "Error when trying to establish the keyboard hook. Global hotkeys will be disabled.",
					"Error occurred", JOptionPane.ERROR_MESSAGE);
			keyboardHook.setHotkeysEnabled(false);
		}
	}
	
	private static void initLookAndFeel()
	{
		LafManager.unregisterTheme(new IntelliJTheme());
		LafManager.registerTheme(new SoulsSpeedrunsTheme());
		LafManager.registerTheme(new DefaultTheme());
		
		LafManager.registerDefaultsAdjustmentTask(new GlobalThemeAdjustmentTask());
		LafManager.registerInitTask(new GlobalThemeInitTask());
		
		LafManager.install(getStoredTheme());
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
		if(game.getDirectory() != null)
			prefs.put(game.getAbbreviation() + PREFS_MODIFIER_GAME_DIR, game.getDirectory().getPath());
		if(game.getSaveFileLocation() != null)
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
	 * Adds a navigation listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addNavigationListener(NavigationListener listener)
	{
		if (listener != null)
			navigationListeners.add(listener);
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
	 * Returns whether a profile is selected.
	 * 
	 * @return whether a profile is selected
	 */
	public static boolean isAProfileSelected()
	{
		return getSelectedProfile().getRoot() != null;
	}


	/**
	 * Imports a new savefile. If a parent is given, it will be imported into that folder. Otherwise the parent will be determined
	 * based on the selection in the save list.
	 * 
	 * @param parentFolder the folder to import the savefile into
	 * @return the imported save
	 */
	public static Save importSavefile(Folder parentFolder)
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
		AbstractMessage.display(AbstractMessage.SUCCESSFUL_IMPORT);
		fireEntryCreatedEvent(newSave);
		return newSave;
	}


	/**
	 * Imports a new savefile and replaces an existing one in the list.
	 * 
	 * @param saveToReplace the save to be replaced by the imported one
	 */
	public static void importAndReplaceSavefile(Save saveToReplace)
	{
		Folder parent = saveToReplace.getParent();
		String name = saveToReplace.getName();
		saveToReplace.delete();
		File saveFile = createFileForNewSave(parent);
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
			JOptionPane.showMessageDialog(mainWindow, "This folder already exists!", "Error occurred", JOptionPane.ERROR_MESSAGE);
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
					"To import a savefile you need to set the savefile location in the profile configuration settings!", "Error occurred",
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
			JOptionPane.showMessageDialog(mainWindow, "Error when trying to import the savefile!", "Error occurred",
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
			gameFile.setWritable(canWriteSaveFile);
			saveFile.setWritable(canWriteSaveFile);
			AbstractMessage.display(AbstractMessage.SUCCESSFUL_LOAD);
		}
		catch (Exception e)
		{
//			JOptionPane.showMessageDialog(mainWindow, "Error when trying to load the savefile!", "Error occurred",
//					JOptionPane.ERROR_MESSAGE);
			AbstractMessage.display(AbstractMessage.FAILED_LOAD);
		}
		fireSaveLoadFinishedEvent(save);
	}


	/**
	 * Opens the SoulsSpeedruns webpage for the Save Organizer in the default browser.
	 */
	public static void openWebPage()
	{
		try
		{
			Desktop.getDesktop().browse(new URI(WEB_PAGE_URL));
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Error occurred", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Error occurred", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	/**
	 * Updates the L&F given the current settings.
	 * Used when a new window is opened to refresh the L&F, otherwise minor things might be off, e.g. the title bar icon.
	 */
	public static void updateLookAndFeel()
	{
		SwingUtilities.invokeLater(() -> {
			LafManager.forceLafUpdate();
		});
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
		if(prefix != PREFS_ERROR_ON_RETRIEVE)
		{
			for (Theme theme : LafManager.getRegisteredThemes())
			{
				if(theme.getPrefix().equalsIgnoreCase(prefix))
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
		if(areGlobalHotkeysEnabled() == flag)
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
		if(isAlwaysOnTop() == flag)
			return;
		prefs.putBoolean(PREFS_KEY_SETTING_ALWAYS_ON_TOP, flag);
		mainWindow.setAlwaysOnTop(flag);
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
		if(isDoubleClickLoadEnabled() == flag)
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
		if(isCheckForUpdatesEnabled() == flag)
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
		if(isCompactModeEnabled() == flag)
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
	 * Switches the gamefile of the currently selected game between read-only and writeable.
	 */
	public static void switchCurrentGameFileWritableState()
	{
		if (!getSelectedGame().supportsReadOnly())
			return;
		File gameFile = getSelectedGame().getSaveFileLocation();
		gameFile.setWritable(!gameFile.canWrite());
		fireGameFileWritableStateChangedEvent(gameFile.canWrite());
	}


	/**
	 * Navigates upwards in the savefile list.
	 */
	public static void navigateToPrevious()
	{
		for(NavigationListener listener : navigationListeners)
		{
			listener.navigatedToPrevious();
		}
	}


	/**
	 * Navigates downwards in the savefile list.
	 */
	public static void navigateToNext()
	{
		for(NavigationListener listener : navigationListeners)
		{
			listener.navigatedToNext();
		}
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
		if(src.getPath().equals(dest.getPath()))
			return;
		if(isDirectoryAParentOfChild(src, dest))
		{
			JOptionPane.showMessageDialog(mainWindow, "The requested action would result in file recursion!", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			throw new IOException();
		}
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
	
	public static boolean isDirectoryAParentOfChild(File possibleParent, File possibleChild)
	{
		File parent = possibleChild.getParentFile();
		while(parent != null)
		{
			if(parent.equals(possibleParent))
				return true;
			parent = parent.getParentFile();
		}
		return false;
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
	 * Copy entry into destination Folder object.
	 * 
	 * @param entry the entry to copy
	 * @param dest the Folder object to paste the file into and attach it to
	 * @param fireCreatedEvent whether to fire a entryCreated event
	 * @throws IOException
	 */
	public static void copyEntry(SaveListEntry entry, Folder dest, boolean fireCreatedEvent) throws IOException
	{
		File src = entry.getFile();
		
		String parentPath = dest.getFile().getPath();
		String name = src.getName();
		
		File newFile = new File(parentPath + File.separator + name);
		for (int i = 0; newFile.exists(); i++)
			newFile = new File(parentPath + File.separator + name + "_" + i);
		
		SaveListEntry newEntry;
		
		if(src.isDirectory())
		{
			copyDirectory(src, newFile);
			newEntry = new Folder(dest, newFile);
		}
		else
		{
			Files.copy(src.toPath(), newFile.toPath());
			newEntry = new Save(dest, newFile);
		}

		dest.addChild(newEntry);
		
		if(fireCreatedEvent)
			fireEntryCreatedEvent(newEntry);
		
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
	 * Gets the major Java version of the current runtime.
	 * 
	 * @return the currently used major Java version
	 */
	public static int getMajorJavaVersion()
	{
		String[] versionElements = System.getProperty("java.version").split("\\.");
	    int firstElement = Integer.parseInt(versionElements[0]);
	    int version = firstElement == 1 ? Integer.parseInt(versionElements[1]) : firstElement;
	    
	    return version;
	}
	


	/**
	 * Checks whether the local Save Organizer version is outdated compared to the latest GitHub release.
	 * 
	 * @return whether the local version is outdated
	 */
	public static boolean isVersionOutdated()
	{
		if(!isCheckForUpdatesEnabled())
			return false;
		
		if(latestReleaseVersion == null)
			latestReleaseVersion = getLatestReleaseVersion();
		
		String[] vals1 = VERSION.split("\\.");
		String[] vals2 = latestReleaseVersion.split("\\.");
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
		{
			String version = latestReleaseJSON.getString("tag_name");
			String prefix = version.split("[0-9]")[0];
			version = version.substring(prefix.length());
			
			return version;
		}
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
		return "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/releases/download/v." + latestVersion + "SoulsSpeedruns.-.Save.Organizer."
				+ latestVersion + ".zip";
	}


	/**
	 * Creates a JSONObject of the latest release on GitHub.
	 * 
	 * @return the JSONObject of the latest release
	 */
	private static JSONObject getLatestReleaseJSON()
	{
		try (InputStream is = URI.create(LATEST_RELEASE_JSON_URL).toURL().openStream())
		{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		}
		catch (Exception e)
		{
			return null;
		}
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

}
