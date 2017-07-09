package com.speedsouls.organizer.main;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.speedsouls.organizer.components.ReadOnlyButton;
import com.speedsouls.organizer.components.SettingsButton;
import com.speedsouls.organizer.content.Folder;
import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.Profile;
import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.content.SaveListEntry;
import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;

import jiconfont.icons.Elusive;
import jiconfont.icons.Iconic;
import jiconfont.swing.IconFontSwing;


/**
 * Bottom segment of the main window.
 * <p>
 * Contains the import, load, read-only and help buttons.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class ButtonPanel extends JPanel
{

	private static final long serialVersionUID = 4450835782973692167L;
	private ReadOnlyButton readOnlyButton;
	private JProgressBar progressBar;


	/**
	 * Creates a new ButtonPanel for the main window.
	 */
	protected ButtonPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		readOnlyButton = new ReadOnlyButton(OrganizerManager.getSelectedGame().getSaveFile(),
				new ImageIcon(OrganizerManager.readOnlyIconMedium));

		progressBar = createProgressBar();
		JButton importButton = createImportButton();
		JButton loadButton = createLoadButton();
		JButton replaceButton = createReplaceButton();
		SettingsButton settingsButton = new SettingsButton();

		Component glue = Box.createHorizontalGlue();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(importButton));

		hGroup.addGroup(layout.createParallelGroup().addComponent(loadButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(replaceButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(progressBar));
		hGroup.addGap(10);
		hGroup.addGroup(layout.createParallelGroup().addComponent(readOnlyButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(glue));
		hGroup.addGroup(layout.createParallelGroup().addComponent(settingsButton));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER).addComponent(importButton).addComponent(loadButton).addComponent(replaceButton)
						.addComponent(progressBar).addComponent(readOnlyButton).addComponent(glue).addComponent(settingsButton));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);

		addButtonListeners(loadButton, replaceButton);
	}


	/**
	 * Creates the progressbar next to the load button.
	 * 
	 * @return the progressbar
	 */
	private JProgressBar createProgressBar()
	{
		JProgressBar progressBar = new JProgressBar();
		progressBar.setMaximumSize(new Dimension(50, 15));
		return progressBar;
	}


	/**
	 * Creates the 'Import current savefile' button.
	 * 
	 * @return the import button
	 */
	private JButton createImportButton()
	{
		JButton importButton = new JButton("Import Savestate");
		importButton.setIcon(IconFontSwing.buildIcon(Iconic.CURVED_ARROW, 16, new Color(30, 144, 255)));
		importButton.addActionListener(event -> {
			Profile profile = OrganizerManager.getSelectedProfile();
			if (profile.getRoot() != null)
			{
				OrganizerManager.importSavefile(null);
				return;
			}
			JOptionPane.showMessageDialog(null, "Create a profile before trying to import a savefile!", "Warning",
					JOptionPane.WARNING_MESSAGE);
		});
		return importButton;
	}


	/**
	 * Creates the 'Load selected savefile' button.
	 * 
	 * @param readOnlyButton the read-only button of this panel
	 * @return the load button
	 */
	private JButton createLoadButton()
	{
		JButton loadButton = new JButton("Load Savestate");
		loadButton.setIcon(IconFontSwing.buildIcon(Elusive.REPEAT, 15, new Color(50, 205, 50)));
		loadButton.addActionListener(event -> {
			SaveListEntry entry = OrganizerManager.getSelectedEntry();
			if (entry instanceof Folder)
				return;
			OrganizerManager.loadSave((Save) entry);
		});
		loadButton.setEnabled(false);
		return loadButton;
	}


	/**
	 * @return
	 */
	private JButton createReplaceButton()
	{
		JButton replaceButton = new JButton("Replace Savestate");
		replaceButton.setIcon(IconFontSwing.buildIcon(Elusive.REFRESH, 15, new Color(255, 165, 0)));
		replaceButton.addActionListener(event -> {
			Save selectedSave = (Save) OrganizerManager.getSelectedEntry();
			int confirm = JOptionPane.showConfirmDialog(getParent(), "Do you really want to delete '" + selectedSave.getName() + "'?",
					"Replace " + selectedSave.getName(), JOptionPane.YES_NO_OPTION);
			if (confirm != 0)
				return;
			OrganizerManager.importAndReplaceSavefile(selectedSave);
		});
		replaceButton.setEnabled(false);
		return replaceButton;
	}


	/**
	 * Adds the listeners to enable/disable the load and replace button
	 * 
	 * @param loadButton the load button
	 */
	private void addButtonListeners(JButton loadButton, JButton replaceButton)
	{
		OrganizerManager.addSaveListener(new SaveListener() {

			@Override
			public void entrySelected(SaveListEntry entry)
			{
				if (entry != null)
				{
					loadButton.setEnabled(entry instanceof Save);
					replaceButton.setEnabled(entry instanceof Save);
					return;
				}
				loadButton.setEnabled(false);
				replaceButton.setEnabled(false);
			}


			@Override
			public void entryCreated(SaveListEntry entry)
			{
			}


			@Override
			public void saveLoadStarted(Save save)
			{
				progressBar.setIndeterminate(true);
			}


			@Override
			public void saveLoadFinished(Save save)
			{
				readOnlyButton.setFile(OrganizerManager.getSelectedGame().getSaveFile());
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run()
					{
						progressBar.setIndeterminate(false);
					}
				}, 200);
			}


			@Override
			public void gameFileWritableStateChanged(boolean writable)
			{
			}

		});

		OrganizerManager.addProfileListener(new ProfileListener() {

			@Override
			public void profileDeleted(Profile profile)
			{
			}


			@Override
			public void changedToProfile(Profile profile)
			{
				loadButton.setEnabled(false);
			}


			@Override
			public void changedToGame(Game game)
			{
				loadButton.setEnabled(false);
			}

		});
	}
}
