package com.speedsouls.organizer.settings;


import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.speedsouls.organizer.about.AboutWindow;
import com.speedsouls.organizer.data.OrganizerManager;


/**
 * SettingsContextMenu.
 * <p>
 * Context menu that appears when pressing the settings button in the main window.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 May 2016
 */
public class SettingsContextMenu extends JPopupMenu implements PopupMenuListener
{

	private static final long serialVersionUID = -4476965394382053981L;

	private SettingsButton button;


	/**
	 * Creates a new context menu for the settings button
	 * 
	 * @param button the button to associate this context menu with
	 */
	public SettingsContextMenu(SettingsButton button)
	{
		super();

		this.button = button;

		JMenuItem itemSettings = createSettingsItem();
		JMenuItem itemAbout = createAboutItem();
		JMenuItem itemHelp = createHelpItem();

		add(itemSettings);
		add(itemAbout);
		add(itemHelp);

		addPopupMenuListener(this);

		show(button, button.getWidth() / 2, button.getHeight() / 2);
		button.setIsPressed(true);
	}


	/**
	 * Creates a new settings item.
	 * 
	 * @return the settings item
	 */
	private JMenuItem createSettingsItem()
	{
		JMenuItem itemSettings = new JMenuItem("Settings");
		itemSettings.addActionListener(event -> {
			new SettingsWindow();
		});
		return itemSettings;
	}


	/**
	 * Creates a new about item.
	 * 
	 * @return the about item
	 */
	private JMenuItem createAboutItem()
	{
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(event -> new AboutWindow());
		return itemAbout;
	}


	/**
	 * Creates a new help item.
	 * 
	 * @return the help item
	 */
	private JMenuItem createHelpItem()
	{
		JMenuItem itemHelp = new JMenuItem("Help");
		itemHelp.setAccelerator(KeyStroke.getKeyStroke("F1"));
		itemHelp.setToolTipText(OrganizerManager.WEB_PAGE_URL);
		itemHelp.addActionListener(event -> {
			OrganizerManager.openWebPage();
		});
		return itemHelp;
	}


	@Override
	public void popupMenuCanceled(PopupMenuEvent e)
	{
	}


	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
	{
		button.setIsPressed(false);
	}


	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e)
	{
	}

}
