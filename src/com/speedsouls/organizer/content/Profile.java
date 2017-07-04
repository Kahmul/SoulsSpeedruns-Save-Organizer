package com.speedsouls.organizer.content;


import java.io.File;


/**
 * Profile class.
 * <p>
 * Class representing the profiles for each game.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class Profile
{

	private String name;
	private Game game;


	/**
	 * Creates a new Profile object with the given name and game.
	 * 
	 * @param name the name of the profile
	 * @param game the game that this profile belongs to
	 */
	public Profile(String name, Game game)
	{
		this.name = name;
		this.game = game;
	}


	/**
	 * @return the name of this profile.
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * @param name the new name of this profile.
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @return the game this profile is used for.
	 */
	public Game getGame()
	{
		return game;
	}


	/**
	 * @param game the game to use this profile for.
	 */
	public void setGame(Game game)
	{
		this.game = game;
	}


	/**
	 * Returns the directory associated with this profile.
	 * 
	 * @return the directory of this profile
	 */
	public File getDirectory()
	{
		return new File(game.getDirectory() + File.separator + getName());
	}

}
