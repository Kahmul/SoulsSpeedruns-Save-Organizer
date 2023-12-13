package com.soulsspeedruns.organizer.main;


import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.update.NewReleaseWindow;


/**
 * The Save Organizer window.
 * <p>
 * Creates and shows the Save Organizer window.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 26 Sep 2015
 */
public class OrganizerWindow extends JFrame
{

	private static final long serialVersionUID = -410330356532830410L;

	private static final int MIN_WIDTH = 550;
	private static final int MIN_HEIGHT = 450;

	private static final boolean IS_RESIZABLE = true;


	public static void main(String[] args)
	{
		new OrganizerWindow();
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
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured", JOptionPane.ERROR_MESSAGE);
		}
		setIconImage(OrganizerManager.soulsspeedrunsIcon);
		setResizable(IS_RESIZABLE);
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		Dimension size = OrganizerManager.getStoredWindowSize();
		setSize(size);
		setLocationRelativeTo(null);
		OrganizerManager.setMainWindow(this);
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
			public void windowGainedFocus(WindowEvent e)
			{
			}


			@Override
			public void windowOpened(WindowEvent e)
			{
				if (OrganizerManager.isVersionOutdated())
					new NewReleaseWindow();
			}


			@Override
			public void windowClosing(WindowEvent e)
			{
				OrganizerManager.setStoredWindowSize(new Dimension(getSize()));
				OrganizerManager.getKeyboardHook().unregisterHook();
				e.getWindow().dispose();
				System.runFinalization();
				System.exit(0);
			}
		});
	}

}
