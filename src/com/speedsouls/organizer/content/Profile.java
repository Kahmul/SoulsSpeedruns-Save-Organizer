package com.speedsouls.organizer.content;


import java.io.File;
import java.util.Collections;


/**
 * Profile class.
 * <p>
 * Class representing the profiles for each game.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class Profile implements Comparable<Profile>
{

	private Game game;
	private Folder root;


	/**
	 * Creates a new Profile object with the given name and game.
	 * 
	 * @param name the name of the profile
	 * @param game the game that this profile belongs to
	 */
	public Profile(String name, Game game)
	{
		this.game = game;
		if (name != null && name.length() > 0)
			root = new Folder(null, new File(game.getDirectory() + File.separator + name));
	}


	/**
	 * @return the name of this profile.
	 */
	public String getName()
	{
		if (root != null)
			return root.getName();
		return "";
	}


	/**
	 * @return the game this profile is used for.
	 */
	public Game getGame()
	{
		return game;
	}


	/**
	 * The root folder containing all folders and savestates for this profile.
	 * 
	 * @return the root folder
	 */
	public Folder getRoot()
	{
		return root;
	}


	/**
	 * Renames the profile to the given name.
	 * 
	 * @param name the new name of this profile
	 */
	public void rename(String name)
	{
		root.rename(name);
		Collections.sort(game.getProfiles());
	}


	/**
	 * Deletes this profile and all of its associated savefiles and folders.
	 */
	public void delete()
	{
		game.removeProfile(this);
		root.delete();
	}


	@Override
	public int compareTo(Profile profile)
	{
		return getName().compareToIgnoreCase(profile.getName());
	}

}
