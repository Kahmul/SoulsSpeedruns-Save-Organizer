/**
 * 
 */
package com.soulsspeedruns.organizer.managers;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.games.Profile;
import com.soulsspeedruns.organizer.games.ds1.DS1ProcessHandler;
import com.soulsspeedruns.organizer.listeners.GameListener;
import com.soulsspeedruns.organizer.listeners.ProfileListener;


/**
 * GamesManager.
 * <p>
 * Offers methods to manage Game and Profile instances at runtime.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 20 Jan 2024
 */
public class GamesManager
{

	private static List<GameListener> gameListeners;
	private static List<ProfileListener> profileListeners;

	private static Game darkSouls;
	private static Game darkSoulsRemastered;
	private static Game darkSouls2;
	private static Game darkSouls2SotFS;
	private static Game darkSouls3;
	private static Game sekiro;
	private static Game eldenRing;


	protected static void initialize()
	{
		gameListeners = new ArrayList<>();
		profileListeners = new ArrayList<>();

		loadGames();
	}


	/**
	 * Creates custom games from the preferences if there are any, and loads preference settings for each game. Then sorts the GAMES list according to
	 * each game's listindex.
	 */
	private static void loadGames()
	{
		createNativeGames();
		createCustomGames();
		for (Game game : Game.GAMES)
			loadGame(game);
		Game.GAMES.sort(null);
	}


	/**
	 * Creates the natively supported Souls games
	 */
	private static void createNativeGames()
	{
		darkSouls = Game.createGame("Dark Souls", "DS1", "DRAKS0005.sl2", "211420",
				"%UserProfile%" + File.separator + "Documents" + File.separator + "NBGI" + File.separator + "DarkSouls", true, false);
		darkSouls.setProcessHandler(DS1ProcessHandler.INSTANCE);

		darkSoulsRemastered = Game.createGame("Dark Souls Remastered", "DSR", "DRAKS0005.sl2", "570940",
				"%UserProfile%" + File.separator + "Documents" + File.separator + "NBGI" + File.separator + "DARK SOULS REMASTERED", false, false);

		darkSouls2 = Game.createGame("Dark Souls II", "DS2", "DARKSII0000.sl2", "236430",
				"%AppData%" + File.separator + "DarkSoulsII" + File.separator + "<SteamID>", true, false);

		darkSouls2SotFS = Game.createGame("Dark Souls II: SotFS", "DS2SOTFS", "DS2SOFS0000.sl2", "335300",
				"%AppData%" + File.separator + "DarkSoulsII" + File.separator + "<SteamID>", true, false);

		darkSouls3 = Game.createGame("Dark Souls III", "DS3", "DS30000.sl2", "374320",
				"%AppData%" + File.separator + "DarkSoulsIII" + File.separator + "<SteamID>", true, false);

		sekiro = Game.createGame("Sekiro", "SSDT", "S0000.sl2", "814380", "%AppData%" + File.separator + "Sekiro" + File.separator + "<SteamID>",
				true, false);

		eldenRing = Game.createGame("Elden Ring", "ER", "ER0000.sl2", "1245620",
				"%AppData%" + File.separator + "EldenRing" + File.separator + "<SteamID>", true, false);
	}


	/**
	 * Creates custom games from the preferences.
	 */
	private static void createCustomGames()
	{
		List<String> gameIDs = SettingsManager.getStoredCustomGameIDs();

		for (String gameID : gameIDs)
		{
			String gameName = SettingsManager.getStoredCustomGameGameName(gameID);
			String saveName = SettingsManager.getStoredCustomGameSaveName(gameID);

			Game.createGame(gameName, gameID, saveName, null, null, true, true);
		}
	}


	/**
	 * (Re)loads the settings saved for the game in the preferences and loads the associated folders and files as profiles and saves.
	 * 
	 * @param game the game for which to load from the preferences
	 */
	private static void loadGame(Game game)
	{
		if (!game.isCustomGame())
		{
			game.setListIndex(SettingsManager.getStoredGameIndex(game));

			String saveLocationPath = SettingsManager.getStoredGameSaveLocation(game);
			if (saveLocationPath == null)
				return;
			game.setSaveFileLocation(new File(saveLocationPath));

			String gameDirectoryPath = SettingsManager.getStoredGameProfilesLocation(game);
			if (gameDirectoryPath == null)
				return;
			game.setDirectory(new File(gameDirectoryPath));

			return;
		}

		game.setListIndex(SettingsManager.getStoredCustomGameIndex(game.getGameID()));

		String saveLocationPath = SettingsManager.getStoredCustomGameSaveLocation(game.getGameID());
		if (saveLocationPath == null)
			return;
		game.setSaveFileLocation(new File(saveLocationPath));

		String gameDirectoryPath = SettingsManager.getStoredCustomGameProfilesLocation(game.getGameID());
		if (gameDirectoryPath == null)
			return;
		game.setDirectory(new File(gameDirectoryPath));
	}


	/**
	 * Refreshes all profiles and games.
	 */
	public static void refreshProfiles()
	{
		loadGames();
	}


	public static Game getEldenRing()
	{
		return eldenRing;
	}


	public static Game getSekiro()
	{
		return sekiro;
	}


	public static Game getDarkSouls3()
	{
		return darkSouls3;
	}


	public static Game getDarkSouls2SotFS()
	{
		return darkSouls2SotFS;
	}


	public static Game getDarkSouls2()
	{
		return darkSouls2;
	}


	public static Game getDarkSoulsRemastered()
	{
		return darkSoulsRemastered;
	}


	public static Game getDarkSouls()
	{
		return darkSouls;
	}


	/**
	 * Gets the first available incremental ID to use for a new custom game.
	 * 
	 * @return first available ID as int
	 */
	public static int getNewCustomGameID()
	{
		List<String> gameIDs = SettingsManager.getStoredCustomGameIDs();

		int id = 0;
		for (String gameID : gameIDs)
		{
			try
			{
				int existingGameID = Integer.parseInt(gameID);
				if (existingGameID != id)
					return id;
				id++;
			}
			catch (NumberFormatException e)
			{
				continue;
			}
		}

		return id;
	}


	/**
	 * Returns the currently selected game.
	 * 
	 * @return the game that is currently selected
	 */
	public static Game getSelectedGame()
	{
		String gameID = SettingsManager.getStoredSelectedGameID();
		List<Game> games = Game.GAMES;
		for (Game game : games)
		{
			if (game.getGameID().equals(gameID))
			{
				return game;
			}
		}
		return games.size() > 0 ? games.get(0) : null;
	}


	/**
	 * Returns the currently selected profile.
	 * 
	 * @return the profile that is currently selected
	 */
	public static Profile getSelectedProfile()
	{
		String profileName = SettingsManager.getStoredSelectedProfileName();
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
	 * Switches the currently selected profile.
	 * 
	 * @param profile the profile to switch to
	 */
	public static void switchToProfile(Profile profile)
	{
		SettingsManager.setStoredSelectedProfileName(profile.getName());
		fireChangedToProfileEvent(profile);
	}


	/**
	 * Switches the currently selected game.
	 * 
	 * @param game the game to switch to
	 */
	public static void switchToGame(Game game)
	{
		SettingsManager.setStoredSelectedGameID(game.getGameID());
		fireChangedToGameEvent(game);
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
	 * Adds a game listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addGameListener(GameListener listener)
	{
		if (listener != null)
			gameListeners.add(listener);
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
	 * Fires a gameCreated event.
	 * 
	 * @param game the game that was created
	 */
	public static void fireGameCreatedEvent(Game game)
	{
		for (GameListener listener : gameListeners)
		{
			listener.gameCreated(game);
		}
	}


	/**
	 * Fires a gameDeleted event.
	 * 
	 * @param game the game that was deleted
	 */
	public static void fireGameDeletedEvent(Game game)
	{
		for (GameListener listener : gameListeners)
		{
			listener.gameDeleted(game);
		}
	}


	/**
	 * Fires a gameEdited event.
	 * 
	 * @param game the game that was edited
	 */
	public static void fireGameEditedEvent(Game game)
	{
		for (GameListener listener : gameListeners)
		{
			listener.gameEdited(game);
		}
	}


	/**
	 * Fires a gameMoved event.
	 * 
	 * @param game     the game that was moved
	 * @param newIndex the new index of the game
	 */
	public static void fireGameMovedEvent(Game game, int newIndex)
	{
		for (GameListener listener : gameListeners)
		{
			listener.gameMoved(game, newIndex);
		}
	}

}
