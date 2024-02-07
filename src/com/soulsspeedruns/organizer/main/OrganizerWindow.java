package com.soulsspeedruns.organizer.main;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.soulsspeedruns.organizer.listeners.SettingsListener;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;


/**
 * The Save Organizer window.
 * <p>
 * Creates and shows the Save Organizer window.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class OrganizerWindow extends JFrame implements SettingsListener
{

	private static final int MIN_WIDTH = 640;
	private static final int MIN_HEIGHT = 550;

	private static final int MIN_WIDTH_COMPACT = 490;
	private static final int MIN_HEIGHT_COMPACT = 400;

	private static final boolean IS_RESIZABLE = true;


	public OrganizerWindow()
	{
		super("SoulsSpeedruns - Save Organizer");

		initProperties();
		initLayout();
		initListeners();

		setVisible(true);
		setExtendedState(SettingsManager.getStoredMaximizedWindowState());
	}


	/**
	 * Sets the properties of the window.
	 */
	private void initProperties()
	{
		OrganizerManager.setMainWindow(this);
		setIconImage(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_MEDIUM));
		setResizable(IS_RESIZABLE);
		
		setAlwaysOnTop(SettingsManager.isAlwaysOnTop());
		setMinSize(SettingsManager.isCompactModeEnabled());
		setSize(SettingsManager.getStoredWindowSize());
		setLocation(SettingsManager.getStoredWindowPosition());
		
		SettingsManager.addSettingsListener(this);
	}


	/**
	 * Adds all components to the layout.
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));

		guiPanel.add(new TopPanel());
		guiPanel.add(new ListPanel());
		guiPanel.add(new ButtonPanel());

		guiPanel.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocusInWindow();
			}
		});

		add(guiPanel);
	}


	/**
	 * Adds all the listeners to the window.
	 */
	private void initListeners()
	{
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowOpened(WindowEvent e)
			{
				requestFocusInWindow();
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.updateComponentTreeUI(OrganizerWindow.this);
				});
			}


			@Override
			public void windowClosing(WindowEvent e)
			{
				int state = getExtendedState();
				if (state != JFrame.MAXIMIZED_BOTH)
					SettingsManager.setStoredWindowSize(new Dimension(getSize()));
				SettingsManager.setStoredMaximizedWindowState(state);
				SettingsManager.setStoredWindowPosition(getLocation());
				SettingsManager.setGlobalHotkeysEnabled(false, false);
				e.getWindow().dispose();
				System.exit(0);
			}
		});
	}


	private void setMinSize(boolean isCompact)
	{
		if (isCompact)
		{
			setMinimumSize(new Dimension(MIN_WIDTH_COMPACT, MIN_HEIGHT_COMPACT));
			return;
		}
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
	}


	@Override
	public void settingChanged(String prefsKey)
	{
		setMinSize(SettingsManager.isCompactModeEnabled());
	}

}
