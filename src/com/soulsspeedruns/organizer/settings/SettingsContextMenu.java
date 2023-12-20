package com.soulsspeedruns.organizer.settings;


import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.soulsspeedruns.organizer.about.AboutWindow;
import com.soulsspeedruns.organizer.data.OrganizerManager;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * SettingsContextMenu. **deprecated/unused**
 * <p>
 * Context menu that appears when pressing the settings button in the main window.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 May 2016
 */
public class SettingsContextMenu extends JPopupMenu
{

	private static final long serialVersionUID = -4476965394382053981L;


	/**
	 * Creates a new context menu for the settings button
	 * 
	 * @param button the button to associate this context menu with
	 */
	public SettingsContextMenu(JButton button)
	{
		super();

		JMenuItem itemSettings = createSettingsItem();
		JMenuItem itemAbout = createAboutItem();
		JMenuItem itemHelp = createHelpItem();

		add(itemSettings);
		add(itemAbout);
		add(itemHelp);

		show(button, button.getWidth() / 2, button.getHeight() / 2);
	}


	/**
	 * Creates a new settings item.
	 * 
	 * @return the settings item
	 */
	private JMenuItem createSettingsItem()
	{
		JMenuItem itemSettings = new JMenuItem("Settings", IconFontSwing.buildIcon(FontAwesome.COG, 17, Color.GRAY));
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
		JMenuItem itemAbout = new JMenuItem("About", new ImageIcon(OrganizerManager.soulsspeedrunsIconSmall));
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
		itemHelp.setMnemonic(KeyEvent.VK_F1);
		itemHelp.setToolTipText(OrganizerManager.WEB_PAGE_URL);
		itemHelp.addActionListener(event -> {
			OrganizerManager.openWebPage();
		});
		return itemHelp;
	}

}
