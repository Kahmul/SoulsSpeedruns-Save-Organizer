package com.soulsspeedruns.organizer.main;


import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.listeners.SettingsListener;


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

	private static final int MIN_WIDTH = 650;
	private static final int MIN_HEIGHT = 550;
	
	private static final int MIN_WIDTH_COMPACT = 500;
	private static final int MIN_HEIGHT_COMPACT = 400;

	private static final boolean IS_RESIZABLE = true;
	

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> {
			new OrganizerWindow();
		});

	}


	public OrganizerWindow()
	{
		super("SoulsSpeedruns - Save Organizer");

		initProperties();
		initLayout();
		initListeners();

		while (!OrganizerManager.isApplicationReady())
		{
		}
		setVisible(true);
	}


	/**
	 * Sets the properties of the window.
	 */
	private void initProperties()
	{
		setIconImage(OrganizerManager.soulsspeedrunsIcon);
		setResizable(IS_RESIZABLE);
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());
		setMinSize(OrganizerManager.isCompactModeEnabled());
		Dimension size = OrganizerManager.getStoredWindowSize();
		setSize(size);
		setLocationRelativeTo(null);
		setExtendedState(OrganizerManager.getStoredMaximizedWindowState());
		OrganizerManager.setMainWindow(this);
		OrganizerManager.addSettingsListener(this);
	}


	/**
	 * Adds all components to the layout.
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));
		
		guiPanel.add(new ProfilePanel());
		guiPanel.add(new SortingPanel());
		guiPanel.add(new ListPanel());
		guiPanel.add(new ButtonPanel());
		
		add(guiPanel);
	}


	/**
	 * Adds all the listeners to the window.
	 */
	private void initListeners()
	{
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowActivated(WindowEvent e)
			{
				requestFocusInWindow();
				OrganizerManager.updateLookAndFeel();
			}


			@Override
			public void windowClosing(WindowEvent e)
			{
				int state = getExtendedState();
				if(state != JFrame.MAXIMIZED_BOTH)
					OrganizerManager.setStoredWindowSize(new Dimension(getSize()));
				OrganizerManager.setStoredMaximizedWindowState(state);
				OrganizerManager.getKeyboardHook().unregisterHook();
				e.getWindow().dispose();
				System.exit(0);
			}
		});
	}
	
	
	private void setMinSize(boolean isCompact)
	{
		if(isCompact)
		{
			setMinimumSize(new Dimension(MIN_WIDTH_COMPACT, MIN_HEIGHT_COMPACT));
			return;
		}
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
	}


	@Override
	public void settingChanged(String prefsKey)
	{
		if(prefsKey.equals(OrganizerManager.PREFS_KEY_SETTING_COMPACT_MODE))
		{
			setMinSize(OrganizerManager.isCompactModeEnabled());
		}
	}

}
