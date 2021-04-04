package com.speedsouls.organizer.profileconfig;


import java.io.File;
import java.util.Collections;

import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.games.Game;
import com.speedsouls.organizer.savelist.Folder;
import com.speedsouls.organizer.savelist.RootFolder;


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

	private final Game game;
	private  RootFolder root;


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
			root = new RootFolder(new File(game.getDirectory() + File.separator + name));
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
		boolean updateSelectedProfile = this.equals(OrganizerManager.getSelectedProfile());
		root.rename(name);
		if (updateSelectedProfile)
			OrganizerManager.switchToProfile(this); // update the name of the stored selected profile
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

	/**
	 * Refresh the profile
	 */
	public void refresh() {
		if (root != null) {
			//force rebuilding of root folder to pick up new files
			root = new RootFolder(new File(game.getDirectory() + File.separator + root.getName()));
		}
	}


	@Override
	public int compareTo(Profile profile)
	{
		return getName().compareToIgnoreCase(profile.getName());
	}

}
