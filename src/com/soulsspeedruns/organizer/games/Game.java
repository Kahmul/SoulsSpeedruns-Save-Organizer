package com.soulsspeedruns.organizer.games;


import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Game Enum.
 * <p>
 * Enum representing the different games with a constant for each.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public enum Game
{

	DARK_SOULS("Dark Souls", "DS1", "DRAKS0005.sl2", true),
	DARK_SOULS_REMASTERED("Dark Souls Remastered", "DSR", "DRAKS0005.sl2", false),
	DARK_SOULS_II("Dark Souls II", "DS2", "DARKSII0000.sl2", true),
	DARK_SOULS_II_SOTFS("Dark Souls II: SotFS", "DS2SOTFS", "DS2SOFS0000.sl2", true),
	DARK_SOULS_III("Dark Souls III", "DS3", "DS30000.sl2", true),
	SEKIRO_SHADOWS_DIE_TWICE("Sekiro", "SSDT", "S0000.sl2", true),
	ELDEN_RING("Elden Ring", "ER", "ER0000.sl2", true);

	private final String caption;
	private final String abbr;
	private final String saveName;
	private final boolean supportsReadOnly;
	private File directory;
	private File saveFile;
	private List<Profile> profiles;


	/**
	 * Creates a new Game constant.
	 * 
	 * @param caption the caption of this game
	 * @param abbr the abbreviation of this game
	 * @param saveName the name of the game's savefile
	 * @param supportsReadOnly whether the game supports read-only
	 */
	private Game(String caption, String abbr, String saveName, boolean supportsReadOnly)
	{
		this.caption = caption;
		this.abbr = abbr;
		this.saveName = saveName;
		this.supportsReadOnly = supportsReadOnly;
		profiles = new CopyOnWriteArrayList<>();
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
