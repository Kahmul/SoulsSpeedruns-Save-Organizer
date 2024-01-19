package com.soulsspeedruns.organizer.settings;


import java.awt.Component;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.soulsspeedruns.organizer.about.AboutWindow;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;


/**
 * ButtonSettingsPanel.
 * <p>
 * Contains the About button.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Jun 2016
 */
public class ButtonsSettingsPanel extends JPanel
{

	/**
	 * Creates a new buttons settings panel.
	 *
	 * @param window the settings window
	 */
	protected ButtonsSettingsPanel(SettingsWindow window)
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		
		JButton aboutButton = createAboutButton();
//		JButton saveButton = createSaveButton(window);
//		JButton cancelButton = createCancelButton(window);

		Component glue = Box.createHorizontalStrut(238);

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addComponent(glue).addComponent(aboutButton);//.addComponent(saveButton).addComponent(cancelButton);

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING).addComponent(glue).addComponent(aboutButton));//.addComponent(saveButton).addComponent(cancelButton));

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}
	
	private JButton createAboutButton()
	{
		JButton aboutButton = new JButton("About", new ImageIcon(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_SMALL)));
		aboutButton.addActionListener(event -> new AboutWindow());
		return aboutButton;
	}


	/**
	 * Creates the save button.
	 * 
	 * @param window the settings window
	 * @return the save button
	 */
//	private JButton createSaveButton(SettingsWindow window)
//	{
//		JButton saveButton = new JButton("Save");
//		saveButton.addActionListener(event -> {
//			window.saveSettings();
//			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
//		});
//		return saveButton;
//	}


	/**
	 * Creates the cancel button.
	 * 
	 * @param window the settings window
	 * @return the cancel button
	 */
//	private JButton createCancelButton(SettingsWindow window)
//	{
//		JButton cancelButton = new JButton("Cancel");
//		cancelButton.addActionListener(event -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
//		return cancelButton;
//	}

}
