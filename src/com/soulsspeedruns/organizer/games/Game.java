package com.soulsspeedruns.organizer.games;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.managers.VersionManager;


/**
 * Game Enum.
 * <p>
 * Enum representing the different games with a constant for each.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class Game implements Comparable<Game>
{

	public static final List<Game> GAMES = new ArrayList<>();

	public static final Game DARK_SOULS = createGame("Dark Souls", "DS1", "DRAKS0005.sl2", "211420", "%UserProfile%\\Documents\\NBGI\\DarkSouls",
			true, false);

	public static final Game DARK_SOULS_REMASTERED = createGame("Dark Souls Remastered", "DSR", "DRAKS0005.sl2", "570940",
			"%UserProfile%\\Documents\\NBGI\\DARK SOULS REMASTERED", false, false);

	public static final Game DARK_SOULS_II = createGame("Dark Souls II", "DS2", "DARKSII0000.sl2", "236430", "%AppData%\\DarkSoulsII\\<SteamID>",
			true, false);

	public static final Game DARK_SOULS_II_SOTFS = createGame("Dark Souls II: SotFS", "DS2SOTFS", "DS2SOFS0000.sl2", "335300",
			"%AppData%\\DarkSoulsII\\<SteamID>", true, false);

	public static final Game DARK_SOULS_III = createGame("Dark Souls III", "DS3", "DS30000.sl2", "374320", "%AppData%\\DarkSoulsIII\\<SteamID>", true,
			false);

	public static final Game SEKIRO_SHADOWS_DIE_TWICE = createGame("Sekiro", "SSDT", "S0000.sl2", "814380", "%AppData%\\Sekiro\\<SteamID>", true,
			false);

	public static final Game ELDEN_RING = createGame("Elden Ring", "ER", "ER0000.sl2", "1245620", "%AppData%\\EldenRing\\<SteamID>", true, false);

	private static final String STEAM_ID_PREFIX = "76561";

	private String caption;
	private final String gameID;
	private String saveName;
	private boolean supportsReadOnly;
	private final boolean isCustomGame;
	private File directory;
	private File saveFile;
	private List<Profile> profiles;

	private int listIndex;

	private final String suggestedSaveLocation;
	private final String steamAppID;


	private Game(String caption, String gameID, String saveName, String steamAppID, String suggestedSaveLocation, boolean supportsReadOnly,
			boolean isCustomGame)
	{
		this.caption = caption;
		this.gameID = gameID;
		this.saveName = saveName;
		this.steamAppID = steamAppID;
		this.suggestedSaveLocation = suggestedSaveLocation;
		this.supportsReadOnly = supportsReadOnly;
		this.isCustomGame = isCustomGame;

		profiles = new ArrayList<>();
	}


	/**
	 * Creates a new Game object.
	 * 
	 * @param caption          the caption of this game
	 * @param gameID           the ID of this game
	 * @param saveName         the name of the game's savefile
	 * @param supportsReadOnly whether the game supports read-only
	 * @param isCustomGame     whether the game was added by the user
	 */
	public static Game createGame(String caption, String gameID, String saveName, String steamAppID, String suggestedSaveLocation,
			boolean supportsReadOnly, boolean isCustomGame)
	{
		Game game = new Game(caption, gameID, saveName, steamAppID, suggestedSaveLocation, supportsReadOnly, isCustomGame);
		GAMES.add(game);
		game.setListIndex(GAMES.size() - 1);

		GamesManager.fireGameCreatedEvent(game);

		return game;
	}


	/**
	 * Removes the game from the list of games.
	 * 
	 * @param game the game to remove
	 */
	public static void deleteGame(Game game)
	{
		if (!game.isCustomGame)
			return;
		GAMES.remove(game);

		SettingsManager.deleteGameProperties(game);

		GamesManager.fireGameDeletedEvent(game);
	}


	/**
	 * Moves the given game to the given new index in the games list.
	 * 
	 * @param gameToMove the game to move
	 * @param newIndex   the new index of the game
	 */
	public static void moveGame(Game gameToMove, int newIndex)
	{
		Game.GAMES.remove(gameToMove);
		Game.GAMES.add(newIndex, gameToMove);

		for (int i = 0; i < GAMES.size(); i++)
		{
			GAMES.get(i).setListIndex(i);
			SettingsManager.storeGameProperties(GAMES.get(i));
		}

		GamesManager.fireGameMovedEvent(gameToMove, newIndex);
	}


	/**
	 * Returns the caption of this game.
	 * 
	 * @return the caption of this game
	 */
	public String getCaption()
	{
		return caption;
	}


	/**
	 * Sets the caption of this game.
	 * 
	 * @param caption
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}


	/**
	 * Returns the ID of this game.
	 * 
	 * @return the ID of this game
	 */
	public String getGameID()
	{
		return gameID;
	}


	/**
	 * Returns the directory associated with this game.
	 * 
	 * @return the directory associated with this game
	 */
	public File getDirectory()
	{
		return directory;
	}


	/**
	 * Sets the directory to associate with this game.
	 * 
	 * @param file the new directory
	 */
	public void setDirectory(File file)
	{
		if (file == null || !file.exists())
			return;
		profiles.clear();
		if (file.isDirectory())
		{
			directory = file;
			File[] files = file.listFiles();
			for (File dir : files)
			{
				if (dir.isDirectory())
				{
					addProfile(new Profile(dir.getName(), this));
				}
			}
		}
	}


	/**
	 * Returns the location of the game's savestate as a File object.
	 * 
	 * @return the location of the game's savestate.
	 */
	public File getSaveFileLocation()
	{
		return saveFile;
	}


	/**
	 * Sets the location of the savefile of this game.
	 * 
	 * @param saveFile the new location
	 */
	public void setSaveFileLocation(File saveFile)
	{
		if (saveFile.getName().equalsIgnoreCase(getSaveName()))
			this.saveFile = saveFile;
	}


	/**
	 * Returns the name of the game's savestate.
	 * 
	 * @return the name of the game's savestate
	 */
	public String getSaveName()
	{
		return saveName;
	}


	/**
	 * Sets the name of the game's savesate.
	 * 
	 * @param saveName the name of the game's savestate
	 */
	public void setSaveName(String saveName)
	{
		this.saveName = saveName;
	}


	/**
	 * Returns whether the game supports read-only.
	 * 
	 * @return whether game supports read-only
	 */
	public boolean supportsReadOnly()
	{
		return supportsReadOnly;
	}


	/**
	 * Sets whether the game supports read-only.
	 * 
	 * @param supportsReadOnly
	 */
	public void setSupportsReadOnly(boolean supportsReadOnly)
	{
		this.supportsReadOnly = supportsReadOnly;
	}


	/**
	 * Returns whether the game is a custom game added by the user.
	 * 
	 * @return true if custom game, false if supported out of the box
	 */
	public boolean isCustomGame()
	{
		return isCustomGame;
	}


	/**
	 * The location at which it is most likely to find the savefile for the given game. By itself not a valid file path. Use
	 * getSuggestedSaveLocationPath instead.
	 * 
	 * @return the suggested save location
	 */
	public String getSuggestedSaveLocation()
	{
		return suggestedSaveLocation;
	}


	/**
	 * Returns the suggested path where one might find the savefile for this game.
	 * 
	 * @return the most likely savefile path
	 */
	public String getSuggestedSaveLocationPath()
	{
		String suggestedPath = getSuggestedSaveLocation();
		if (suggestedPath == null || suggestedPath.equals(""))
			return "";

		if (VersionManager.isRunningOnWindows())
		{
			String appdataPath = System.getenv("appdata");
			String userprofilePath = System.getenv("userprofile");

			suggestedPath = suggestedPath.replace("%AppData%", appdataPath);
			suggestedPath = suggestedPath.replace("%UserProfile%", userprofilePath);

			suggestedPath = suggestedPath.replace("\\<SteamID>", "");

			suggestedPath += getSteamIDFolderName(suggestedPath);

			return suggestedPath;
		}

		if (VersionManager.isRunningOnLinux())
		{
			String prefix = "~\\.local\\share\\Steam\\steamapps\\compatdata\\" + steamAppID + "\\pfx\\drive_c\\users\\steamuser\\";

			suggestedPath = suggestedPath.replace("%AppData%", prefix);
			suggestedPath = suggestedPath.replace("%UserProfile%", prefix);

			suggestedPath = suggestedPath.replace("\\<SteamID>", "");

			suggestedPath += getSteamIDFolderName(suggestedPath);
		}

		return suggestedPath;
	}


	/**
	 * Returns the name of the folder with the user steam ID, if it exists.
	 * 
	 * @param pathToDir the path to the directory in which to search for the steam ID folder
	 * @return the folder named after the user's steam ID, if it exists
	 */
	private static String getSteamIDFolderName(String pathToDir)
	{
		File dir = new File(pathToDir);
		if (!dir.exists() || !dir.isDirectory())
			return "";

		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isDirectory() && file.getName().startsWith(STEAM_ID_PREFIX))
				return "\\" + file.getName();
		}
		
		return "";
	}


	/**
	 * Either returns the path of the savefile associated with this game, or a suggested path where it might be found if no file is set.
	 * 
	 * @return the savefile path or the path where one might find it
	 */
	public String getSaveFilePathOrSuggested()
	{
		if (saveFile != null)
			return saveFile.getPath();
		return getSuggestedSaveLocationPath();
	}


	/**
	 * Returns the position of this game in the game list.
	 * 
	 * @return the index of the game in the game list.
	 */
	public int getListIndex()
	{
		return listIndex;
	}


	/**
	 * Sets the index of this game in the game list.
	 * 
	 * @param index the new index
	 */
	public void setListIndex(int index)
	{
		this.listIndex = index;
	}


	/**
	 * @return the profiles
	 */
	public List<Profile> getProfiles()
	{
		return profiles;
	}


	/**
	 * @param profile
	 */
	public void addProfile(Profile profile)
	{
		profiles.add(profile);
		Collections.sort(profiles);
	}


	/**
	 * @param profile
	 */
	public void removeProfile(Profile profile)
	{
		profiles.remove(profile);
		Collections.sort(profiles);
	}


	@Override
	public int compareTo(Game o)
	{
		return Integer.compare(listIndex, o.getListIndex());
	}

}
