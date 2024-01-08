package com.soulsspeedruns.organizer.profileconfig;


import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


/**
 * Window for configuring profiles.
 * <p>
 * Allows the configuration of existing and creation of new profiles.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class ProfileConfigurationWindow extends JDialog
{

	/**
	 * Creates a new configuration window.
	 */
	public ProfileConfigurationWindow()
	{
		super(OrganizerManager.getMainWindow(), "Profile Configuration", Dialog.ModalityType.APPLICATION_MODAL);

		initLayout();
		initProperties();

		setVisible(true);
	}


	/**
	 * Sets the properties of the window.
	 */
	private void initProperties()
	{
		pack();
		setSize(600, this.getHeight());
		setResizable(false);
		setLocationRelativeTo(null);
		setIconImage(OrganizerManager.soulsspeedrunsIcon);
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowActivated(WindowEvent e)
			{
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.updateComponentTreeUI(ProfileConfigurationWindow.this);
				});
			}
		});
	}


	/**
	 * Adds all components to the layout.
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));
		
		JPanel buttonPanel = createButtonsPanel();
		JTabbedPane tabbedPane = createGamesTabbedPane();
		
		guiPanel.add(buttonPanel);
		guiPanel.add(tabbedPane);
		
		guiPanel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocusInWindow();
			}
		});

		add(guiPanel);
	}
	
	
	/**
	 * Creates the panel with the Add Game and Edit Game buttons.
	 * 
	 * @return the buttons panel
	 */
	private JPanel createButtonsPanel()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		Color buttonColor = new Color(UIManager.getColor("background").getRGB());
		
		JButton addButton = new JButton("Add Game", IconFontSwing.buildIcon(FontAwesome.PLUS, 16, (Color) UIManager.get("hyperlink")));
		addButton.setBorderPainted(false);
		addButton.setBackground(buttonColor);
		
		JButton editButton = new JButton("Edit Game", IconFontSwing.buildIcon(FontAwesome.PENCIL_SQUARE, 16, (Color) UIManager.get("hyperlink")));
		editButton.setBorderPainted(false);
		editButton.setBackground(buttonColor);
		
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		
		return buttonPanel;
	}
	
	
	/**
	 * Creates the tabbed pane with all the games.
	 * 
	 * @return the games tabbed pane
	 */
	private JTabbedPane createGamesTabbedPane()
	{
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		Game[] games = Game.values();
		for (Game game : games)
		{
			GameConfigPanel panel = new GameConfigPanel(game);
			tabbedPane.add(panel, game.getCaption());
			if (OrganizerManager.getSelectedGame().equals(game))
				tabbedPane.setSelectedComponent(panel);
		}
		
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		return tabbedPane;
	}

}
