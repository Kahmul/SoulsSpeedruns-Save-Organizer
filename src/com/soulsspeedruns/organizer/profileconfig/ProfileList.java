package com.soulsspeedruns.organizer.profileconfig;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.listeners.ProfileListener;


/**
 * ProfileList.
 * <p>
 * Displays Profile objects in a JList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 28 Sep 2015
 */
public class ProfileList extends JList<Profile> implements ProfileListener, KeyListener
{

	private Game game;


	/**
	 * Creates a profile list for the given game.
	 * 
	 * @param game the game to create this list for
	 */
	public ProfileList(Game game)
	{
		super();

		this.game = game;

		setCellRenderer(new ProfileListRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		addKeyListener(this);
		OrganizerManager.addProfileListener(this);

		setModel(new DefaultListModel<>());
		fillWith(game.getProfiles());
	}
	
	
	/**
	 * The game associated with this profile list
	 * 
	 * @return the game
	 */
	public Game getGame()
	{
		return game;
	}


	/**
	 * Fills the list with the given profiles.
	 * 
	 * @param profileList the list of profiles to fill this ProfileList with
	 */
	public void fillWith(List<Profile> profileList)
	{
		Profile selectedProfile = getSelectedValue();
		DefaultListModel<Profile> model = (DefaultListModel<Profile>) getModel();
		model.removeAllElements();
		for (Profile profile : profileList)
			model.addElement(profile);
		int selectedIndex = model.indexOf(selectedProfile);
		if (selectedIndex != -1)
			setSelectedIndex(selectedIndex);
	}


	/**
	 * Asks to import profiles from the filesystem to this list.
	 */
	public void askToImportProfiles()
	{
		if (game.getDirectory() == null)
			return;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(true);
		int val = fc.showOpenDialog(SwingUtilities.windowForComponent(this));
		if (val == JFileChooser.APPROVE_OPTION)
		{
			File[] files = fc.getSelectedFiles();
			
			for (File file : files)
			{
				try
				{
					if (!file.isDirectory())
						continue;
					
					File dest = new File(game.getDirectory() + File.separator + file.getName());
					if(file.getPath().equals(dest.getPath())) // Profile already exists
						continue;
					
					OrganizerManager.copyDirectory(file, dest);
					Profile newProfile = new Profile(file.getName(), game);
					game.addProfile(newProfile);
					OrganizerManager.fireProfileCreatedEvent(newProfile);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(OrganizerManager.getMainWindow(), "Error when trying to import the profiles!", "Error occurred",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			
			fillWith(game.getProfiles());
		}
	}


	/**
	 * Asks to create a new profile for this game.
	 */
	public void askToCreateProfile()
	{
		if (game.getDirectory() == null)
			return;
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		String name = JOptionPane.showInputDialog(SwingUtilities.windowForComponent(this), "Profile name: ", "Create Profile", JOptionPane.QUESTION_MESSAGE);
		boolean nameValidation = validateNameForNewProfile(name);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (nameValidation)
		{
			try
			{
				createNewProfile(name);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(getParent(), "Error occurred when trying to create the profile!", "Error occurred",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	/**
	 * Creates a new profile with the given name.
	 * 
	 * @param name the name of the profile
	 */
	private void createNewProfile(String name)
	{
		new File(game.getDirectory() + File.separator + name).mkdirs();
		Profile newProfile = new Profile(name, game);
		game.addProfile(newProfile);
		fillWith(game.getProfiles());
		OrganizerManager.fireProfileCreatedEvent(newProfile);
	}


	/**
	 * Validates the potential name for a new profile.
	 * 
	 * @param name the name
	 * @return whether the name is valid
	 */
	private boolean validateNameForNewProfile(String name)
	{
		if (name == null)
			return false;
		name = name.trim();
		if (name.length() < 1)
			return false;
		if (OrganizerManager.containsIllegals(name))
		{
			JOptionPane.showMessageDialog(getParent(), "Illegal characters (" + OrganizerManager.ILLEGAL_CHARACTERS + ") are not allowed!",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		File newSaveDir = new File(game.getDirectory() + File.separator + name);
		if (newSaveDir.exists())
		{
			JOptionPane.showMessageDialog(getParent(), "This profile already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}


	/**
	 * Asks to delete the given list of profiles.
	 * 
	 * @param profiles the list of profiles
	 */
	public void askToDeleteProfiles(List<Profile> profiles)
	{
		if (profiles == null)
			return;
		int confirm = -1;
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		if (profiles.size() == 1)
			confirm = JOptionPane.showConfirmDialog(getParent(),
					"Do you really want to delete '" + profiles.get(0).getName() + "' and all of its contents?", "Delete",
					JOptionPane.YES_NO_OPTION);
		else if (profiles.size() > 1)
			confirm = JOptionPane.showConfirmDialog(getParent(), "Do you really want to delete all your selected profiles?", "Delete",
					JOptionPane.YES_NO_OPTION);
		if (confirm == 0)
			deleteProfiles(profiles);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	/**
	 * Deletes the given list of profiles.
	 * 
	 * @param profiles the profiles to delete
	 */
	private void deleteProfiles(List<Profile> profiles)
	{
		DefaultListModel<Profile> model = (DefaultListModel<Profile>) getModel();
		for (Profile profile : profiles)
		{
			profile.delete();
			model.removeElement(profile);
			OrganizerManager.fireProfileDeletedEvent(profile);
		}
		repaint();
	}


	/**
	 * Asks to edit the given profile.
	 * 
	 * @param profile the profile to edit
	 */
	public void askToEditProfile(Profile profile)
	{
		if (profile == null)
			return;
		boolean areHotkeysEnabled = OrganizerManager.getKeyboardHook().areHotkeysEnabled();
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(false);
		String newProfileName = (String) JOptionPane.showInputDialog(SwingUtilities.windowForComponent(this), "Profile name: ", "Edit " + profile.getName(),
				JOptionPane.QUESTION_MESSAGE, null, null, profile.getName());
		boolean nameValidation = validateNewName(profile, newProfileName);
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
		if (nameValidation)
			renameProfile(profile, newProfileName);
	}


	/**
	 * Renames the profile with the given name and sorts the list afterwards.
	 * 
	 * @param profile the profile to rename
	 * @param newName the new name
	 */
	private void renameProfile(Profile profile, String newName)
	{
		profile.rename(newName);
		repaint();
	}


	/**
	 * Validates the new name given to a profile.
	 * 
	 * @param profile the profile
	 * @param newName the new name
	 * @return whether the new name is valid
	 */
	private boolean validateNewName(Profile profile, String newName)
	{
		if (newName == null)
			return false;
		newName = newName.trim();
		if (newName.length() < 1 || newName.equals(profile.getName()))
			return false;
		if (OrganizerManager.containsIllegals(newName))
		{
			JOptionPane.showMessageDialog(getParent(), "Illegal characters (" + OrganizerManager.ILLEGAL_CHARACTERS + ") are not allowed!",
					"Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		// if the name exists and the renaming is not a re-capitalization then don't allow renaming
		File newSaveDir = new File(game.getDirectory() + File.separator + newName);
		if (newSaveDir.exists() && !profile.getName().equalsIgnoreCase(newName))
		{
			JOptionPane.showMessageDialog(getParent(), "This name already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}


	@Override
	public void profileDeleted(Profile profile)
	{
	}


	@Override
	public void profileCreated(Profile profile)
	{
	}


	@Override
	public void profileDirectoryChanged(Game game)
	{
		if (this.game.equals(game))
			fillWith(game.getProfiles());
	}


	@Override
	public void changedToProfile(Profile profile)
	{
	}


	@Override
	public void changedToGame(Game game)
	{
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
	}


	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_F2)
			askToEditProfile(getSelectedValue());
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
			askToDeleteProfiles(getSelectedValuesList());
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
