package com.speedsouls.organizer.content;


import java.io.File;


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

	DARK_SOULS("Dark Souls", "DS1", "DRAKS0005.sl2"),
	DARK_SOULS_II("Dark Souls II", "DS2", "DARKSII0000.sl2"),
	DARK_SOULS_II_SOTFS("Dark Souls II: SotFS", "DS2SOTFS", "DS2SOFS0000.sl2"),
	DARK_SOULS_III("Dark Souls III", "DS3", "DS30000.sl2");

	private String caption;
	private String abbr;
	private String saveName;
	private File directory;


	/**
	 * Creates a new Game constant.
	 * 
	 * @param caption the caption of this game
	 * @param abbr the abbreviation of this game
	 * @param saveName the name of the game's savefile
	 */
	private Game(String caption, String abbr, String saveName)
	{
		this.caption = caption;
		this.abbr = abbr;
		this.saveName = saveName;
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
		if (file.isDirectory())
			directory = file;
	}


	/**
	 * Returns the file of the game's savestate.
	 * 
	 * @return the file of the game's savestate.
	 */
	public File getSaveFile()
	{
		return new File(getDirectory() + File.separator + getSaveName());
	}


	/**
	 * Returns the name of the game's savestate.
	 * 
	 * @return the name of the game's savestate.
	 */
	public String getSaveName()
	{
		return saveName;
	}

}
