package com.soulsspeedruns.organizer.main;


import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.event.ThemeChangeEvent;
import com.github.weisj.darklaf.theme.event.ThemeChangeListener;
import com.soulsspeedruns.organizer.components.HyperLink;
import com.soulsspeedruns.organizer.components.ReadOnlyButton;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.games.Profile;
import com.soulsspeedruns.organizer.listeners.ProfileListener;
import com.soulsspeedruns.organizer.listeners.SaveListener;
import com.soulsspeedruns.organizer.listeners.SettingsListener;
import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SavesManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.managers.VersionManager;
import com.soulsspeedruns.organizer.savelist.Folder;
import com.soulsspeedruns.organizer.savelist.Save;
import com.soulsspeedruns.organizer.savelist.SaveListEntry;
import com.soulsspeedruns.organizer.settings.SettingsWindow;

import jiconfont.icons.Elusive;
import jiconfont.icons.FontAwesome;
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

	private JButton importButton;
	private JButton loadButton;
	private JButton replaceButton;
	private JButton settingsButton;
	private HyperLink updateLink;


	/**
	 * Creates a new ButtonPanel for the main window.
	 */
	protected ButtonPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		ReadOnlyButton readOnlyButton = new ReadOnlyButton(GamesManager.getSelectedGame().getSaveFileLocation());

		importButton = createImportButton();
		loadButton = createLoadButton();
		replaceButton = createReplaceButton();
		settingsButton = createSettingsButton();
		updateLink = createUpdateLink();

		JPanel settingsUpdatePanel = new JPanel();
		GroupLayout panelLayout = new GroupLayout(settingsUpdatePanel);

		panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup().addComponent(updateLink).addGap(10).addComponent(settingsButton));
		panelLayout.setVerticalGroup(panelLayout.createParallelGroup(Alignment.CENTER).addComponent(updateLink).addComponent(settingsButton));

		settingsUpdatePanel.setLayout(panelLayout);

		Component glue = Box.createHorizontalGlue();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addComponent(importButton);

		hGroup.addComponent(loadButton);
		hGroup.addComponent(replaceButton);
		hGroup.addGap(10);
		hGroup.addComponent(readOnlyButton);
		hGroup.addComponent(glue);
		hGroup.addComponent(settingsUpdatePanel);

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(importButton).addComponent(loadButton).addComponent(replaceButton)
				.addComponent(readOnlyButton).addComponent(glue).addComponent(settingsUpdatePanel));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);

		addButtonListeners();
		refreshButtons();
	}


	/**
	 * Creates the 'Import Savestate' button.
	 * 
	 * @return the import button
	 */
	private JButton createImportButton()
	{
		JButton importButton = new JButton("Import Savestate");
//		importButton.setIcon(IconFontSwing.buildIcon(Iconic.CURVED_ARROW, 16, Color.decode("0x1d6fbe")));
		importButton.setIcon(IconsAndFontsManager.getImportIcon(IconsAndFontsManager.ICON_SIZE_SMALL));
		importButton.addActionListener(event -> {
			if (GamesManager.isAProfileSelected())
			{
				SavesManager.importSavefile(null);
				return;
			}
			JOptionPane.showMessageDialog(OrganizerManager.getMainWindow(),
					"You need to set up your game and create a profile before you can import a savefile. You can do this in the game configuration settings in the top right.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		});
		return importButton;
	}


	/**
	 * Creates the 'Load Savestate' button.
	 * 
	 * @param readOnlyButton the read-only button of this panel
	 * @return the load button
	 */
	private JButton createLoadButton()
	{
		JButton loadButton = new JButton("Load Savestate");
		loadButton.setIcon(IconFontSwing.buildIcon(Elusive.REPEAT, 15, Color.decode("0x2c9558")));
		loadButton.setDisabledIcon(IconFontSwing.buildIcon(Elusive.REPEAT, 15, UIManager.getColor("disabledIconColor")));
		loadButton.addActionListener(event -> {
			SaveListEntry entry = SavesManager.getSelectedEntry();
			if (entry instanceof Folder)
				return;
			SavesManager.loadSave((Save) entry);
		});
		LafManager.addThemeChangeListener(new ThemeChangeListener()
		{

			@Override
			public void themeInstalled(ThemeChangeEvent e)
			{
				loadButton.setDisabledIcon(IconFontSwing.buildIcon(Elusive.REPEAT, 15, UIManager.getColor("disabledIconColor")));
			}


			@Override
			public void themeChanged(ThemeChangeEvent e)
			{
			}
		});
		loadButton.setEnabled(false);
		return loadButton;
	}


	/**
	 * Creates the 'Replace Savestate' button.
	 * 
	 * @return the replace button
	 */
	private JButton createReplaceButton()
	{
		JButton replaceButton = new JButton("Replace Savestate");
		replaceButton.setIcon(IconFontSwing.buildIcon(Elusive.REFRESH, 15, Color.decode("0xeb751c")));
		replaceButton.setDisabledIcon(IconFontSwing.buildIcon(Elusive.REFRESH, 15, UIManager.getColor("disabledIconColor")));
		replaceButton.addActionListener(event -> {
			Save selectedSave = (Save) SavesManager.getSelectedEntry();
			int confirm = JOptionPane.showConfirmDialog(getParent(), "Do you really want to replace '" + selectedSave.getName() + "'?",
					"Replace " + selectedSave.getName(), JOptionPane.YES_NO_OPTION);
			if (confirm != 0)
				return;
			SavesManager.importAndReplaceSavefile(selectedSave);
		});
		LafManager.addThemeChangeListener(new ThemeChangeListener()
		{

			@Override
			public void themeInstalled(ThemeChangeEvent e)
			{
				replaceButton.setDisabledIcon(IconFontSwing.buildIcon(Elusive.REFRESH, 15, UIManager.getColor("disabledIconColor")));
			}


			@Override
			public void themeChanged(ThemeChangeEvent e)
			{
			}
		});
		replaceButton.setEnabled(false);
		return replaceButton;
	}


	private HyperLink createUpdateLink()
	{
		HyperLink updateLink = new HyperLink("Update Available", VersionManager.getLatestReleaseURL());
		updateLink.setHorizontalAlignment(SwingConstants.RIGHT);
		updateLink.setVisible(VersionManager.isVersionOutdated());

		return updateLink;
	}


	/**
	 * Creates the settings button.
	 * 
	 * @return the settings button
	 */
	private JButton createSettingsButton()
	{
		JButton settingsButton = new JButton(IconFontSwing.buildIcon(FontAwesome.COG, 22, Color.GRAY));
		settingsButton.addActionListener(event -> new SettingsWindow());
		settingsButton.setFocusable(false);
		return settingsButton;
	}


	/**
	 * Refreshes the button texts based on settings.
	 */
	private void refreshButtons()
	{
		boolean isCompact = SettingsManager.isCompactModeEnabled();
		importButton.setText(isCompact ? "Import" : "Import Savestate");
		loadButton.setText(isCompact ? "Load" : "Load Savestate");
		replaceButton.setText(isCompact ? "Replace" : "Replace Savestate");
	}


	/**
	 * Adds the listeners to manipulate the buttons.
	 */
	private void addButtonListeners()
	{
		SavesManager.addSaveListener(new SaveListener()
		{

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
			public void entryRenamed(SaveListEntry entry)
			{
			}


			@Override
			public void saveLoadStarted(Save save)
			{
			}


			@Override
			public void saveLoadFinished(Save save)
			{
			}


			@Override
			public void gameFileWritableStateChanged(boolean writable)
			{
			}

		});

		GamesManager.addProfileListener(new ProfileListener()
		{

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

		SettingsManager.addSettingsListener(new SettingsListener()
		{

			@Override
			public void settingChanged(String prefsKey)
			{
				updateLink.setVisible(VersionManager.isVersionOutdated());
				refreshButtons();
			}
		});
	}
}
