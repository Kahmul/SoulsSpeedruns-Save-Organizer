package com.soulsspeedruns.organizer.settings;


import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;


/**
 * SettingsWindow.
 * <p>
 * Allows the user to configure various settings.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 3 Jun 2016
 */
public class SettingsWindow extends JDialog
{

	private GeneralSettingsPanel generalSettingsPanel;
	private HotkeysSettingsPanel hotkeysSettingsPanel;


	/**
	 * Creates a new SettingsWindow.
	 */
	public SettingsWindow()
	{
		super(OrganizerManager.getMainWindow(), "Settings", Dialog.ModalityType.APPLICATION_MODAL);

		initLayout();
		initProperties();
		
		setVisible(true);
	}


	/**
	 * Inits the properties.
	 */
	private void initProperties()
	{
		pack();
		setResizable(false);
		SettingsManager.getKeyboardHook().unregisterHook();
		setIconImage(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_MEDIUM));
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setAlwaysOnTop(SettingsManager.isAlwaysOnTop());
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocusInWindow();
			}
		});
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e)
			{
				requestFocusInWindow();
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.updateComponentTreeUI(SettingsWindow.this);
				});
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				saveSettings();
				SettingsManager.getKeyboardHook().registerHook();
			}
		});
	}


	/**
	 * Inits the layout.
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));
		guiPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		generalSettingsPanel = new GeneralSettingsPanel();
		hotkeysSettingsPanel = new HotkeysSettingsPanel();
		guiPanel.add(generalSettingsPanel);
		guiPanel.add(hotkeysSettingsPanel);
		guiPanel.add(new ButtonsSettingsPanel(this));

		add(guiPanel);
	}


	/**
	 * Saves the changes to the settings.
	 */
	protected void saveSettings()
	{
		generalSettingsPanel.applyChanges();
		hotkeysSettingsPanel.applyChanges();
	}

}
