package com.soulsspeedruns.organizer.games;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Game Enum.
 * <p>
 * Enum representing the different games with a constant for each.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class Game
{


	public static final List<Game> GAMES = new ArrayList<>();

	public static final Game DARK_SOULS = createGame("Dark Souls", "DS1", "DRAKS0005.sl2", true, false);
	public static final Game DARK_SOULS_REMASTERED = createGame("Dark Souls Remastered", "DSR", "DRAKS0005.sl2", false, false);
	public static final Game DARK_SOULS_II = createGame("Dark Souls II", "DS2", "DARKSII0000.sl2", true, false);
	public static final Game DARK_SOULS_II_SOTFS = createGame("Dark Souls II: SotFS", "DS2SOTFS", "DS2SOFS0000.sl2", true, false);
	public static final Game DARK_SOULS_III = createGame("Dark Souls III", "DS3", "DS30000.sl2", true, false);
	public static final Game SEKIRO_SHADOWS_DIE_TWICE = createGame("Sekiro", "SSDT", "S0000.sl2", true, false);
	public static final Game ELDEN_RING = createGame("Elden Ring", "ER", "ER0000.sl2", true, false);

	private final String caption;
	private final String abbr;
	private final String saveName;
	private final boolean supportsReadOnly;
	private final boolean isCustomGame;
	private File directory;
	private File saveFile;
	private List<Profile> profiles;


	private Game(String caption, String abbr, String saveName, boolean supportsReadOnly, boolean isCustomGame)
	{
		this.caption = caption;
		this.abbr = abbr;
		this.saveName = saveName;
		this.supportsReadOnly = supportsReadOnly;
		this.isCustomGame = isCustomGame;

		profiles = new ArrayList<>();
	}


	/**
	 * Creates a new Game object.
	 * 
	 * @param caption          the caption of this game
	 * @param abbr             the abbreviation of this game
	 * @param saveName         the name of the game's savefile
	 * @param supportsReadOnly whether the game supports read-only
	 * @param isCustomGame     whether the game was added by the user
	 */
	public static Game createGame(String caption, String abbr, String saveName, boolean supportsReadOnly, boolean isCustomGame)
	{
		Game game = new Game(caption, abbr, saveName, supportsReadOnly, isCustomGame);
		GAMES.add(game);

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
	}

//	/**
//	 * Returns a combined list object consisting of the games support from the start
//	 * 
//	 * @return
//	 */
//	public static List<Game> all()
//	{
//		List<Game> gamesList = new ArrayList<>(BASE_GAMES.size() + CUSTOM_GAMES.size());
//		gamesList.addAll(BASE_GAMES);
//		gamesList.addAll(CUSTOM_GAMES);
//		
//		return gamesList;
//	}


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
	 * Returns the abbreviation of this game.
	 * 
	 * @return the abbreviation of this game
	 */
	public String getAbbreviation()
	{
		return abbr;
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
	 * Returns whether the game supports read-only.
	 * 
	 * @return whether game supports read-only
	 */
	public boolean supportsReadOnly()
	{
		return supportsReadOnly;
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

}
