package com.speedsouls.organizer.components;


import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.Profile;
import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.listeners.ProfileListener;


/**
 * ProfileList.
 * <p>
 * Displays Profile objects in a JList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 28 Sep 2015
 */
public class ProfileList extends JList<Profile> implements ListCellRenderer<Profile>, ProfileListener, KeyListener
{

	private static final long serialVersionUID = -3296536317277215330L;
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

		setCellRenderer(this);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		addKeyListener(this);
		OrganizerManager.addProfileListener(this);

		setModel(new DefaultListModel<>());
		fillWith(OrganizerManager.getProfiles(game));
	}


	/**
	 * Fills the list with the given profiles.
	 * 
	 * @param profileList the list of profiles to fill this ProfileList with
	 */
	public void fillWith(List<Profile> profileList)
	{
		DefaultListModel<Profile> model = (DefaultListModel<Profile>) getModel();
		model.removeAllElements();
		for (int i = 0; i < profileList.size(); i++)
		{
			model.add(i, profileList.get(i));
		}
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
		int val = fc.showOpenDialog(getParent());
		if (val == JFileChooser.APPROVE_OPTION)
		{
			OrganizerManager.importAsProfiles(fc.getSelectedFiles(), game);
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
		String name = JOptionPane.showInputDialog(getParent(), "Profile name: ", "Create Profile", JOptionPane.QUESTION_MESSAGE);
		if (name == null || name.length() < 1)
		{
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
			return;
		}
		name = name.trim();
		Profile profile = new Profile(name, game);
		File profileFile = profile.getDirectory();
		if (profileFile.exists())
		{
			JOptionPane.showMessageDialog(getParent(), "This profile already exists!", "Error occured", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			profileFile.mkdirs();
			OrganizerManager.updateProfiles(game);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(getParent(), "Error occured when trying to create the profile!", "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
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
		String newProfileName = (String) JOptionPane.showInputDialog(getParent(), "Profile name: ", "Edit " + profile.getName(),
				JOptionPane.QUESTION_MESSAGE, null, null, profile.getName());
		if (newProfileName == null || newProfileName.length() < 1)
		{
			OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
			return;
		}
		newProfileName = newProfileName.trim();
		File newProfileDir = new File(profile.getDirectory().getParentFile() + File.separator + newProfileName);
		profile.getDirectory().renameTo(newProfileDir);
		OrganizerManager.updateProfiles(profile.getGame());
		OrganizerManager.getKeyboardHook().setHotkeysEnabled(areHotkeysEnabled);
	}


	/**
	 * Asks to delete the given list of profiles
	 * 
	 * @param profiles the list of profiles
	 */
	public void askToDeleteProfiles(List<Profile> profiles)
	{
		int confirm = -1;
		if (profiles.size() == 1)
			confirm = JOptionPane.showConfirmDialog(getParent(),
					"Do you really want to delete '" + profiles.get(0).getName() + "' and all of its contents?", "Delete",
					JOptionPane.YES_NO_OPTION);
		else if (profiles.size() > 1)
			confirm = JOptionPane.showConfirmDialog(getParent(), "Do you really want to delete all your selected profiles?", "Delete",
					JOptionPane.YES_NO_OPTION);
		if (confirm == 0)
		{
			for (Profile profile : profiles)
			{
				OrganizerManager.deleteDirectory(profile.getDirectory());
				OrganizerManager.updateProfiles(profile.getGame());
			}
		}
	}


	@Override
	public Component getListCellRendererComponent(JList<? extends Profile> list, Profile profile, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, profile, index, isSelected, cellHasFocus);
		if (profile != null)
		{
			label.setText(profile.getName());
			label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		}
		return label;
	}


	@Override
	public void profilesUpdated(Game game)
	{
		if (this.game != game)
			return;
		fillWith(OrganizerManager.getProfiles(game));
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
	public void addedToProfile(Save save, Profile profile)
	{
	}


	@Override
	public void removedFromProfile(Save save, Profile profile)
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
