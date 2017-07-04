package com.speedsouls.organizer.config;


import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.data.OrganizerManager;


/**
 * Directory part of the configuration window.
 * <p>
 * Contains the directory field as well as the browse button.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 28 Sep 2015
 */
public class GameConfigDirectoryPanel extends JPanel
{

	private static final long serialVersionUID = 3869667597583441534L;


	/**
	 * Creates a new directory panel.
	 * 
	 * @param game the game of this panel
	 */
	protected GameConfigDirectoryPanel(Game game)
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		File gameDir = game.getDirectory();

		JLabel directoryLabel = new JLabel("Directory:");
		JTextField directoryField = new JTextField(gameDir != null ? gameDir.getPath() : "");
		JButton browseButton = createBrowseButton(directoryField, game);

		directoryField.setEditable(false);

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(directoryLabel).addComponent(directoryField));
		hGroup.addGroup(layout.createParallelGroup().addComponent(browseButton));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(directoryLabel));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(directoryField).addComponent(browseButton));

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}


	/**
	 * Creates the browse button.
	 * 
	 * @param directoryField the directory field of this panel
	 * @param game the game associated with this panel
	 * @return the browse button
	 */
	private JButton createBrowseButton(JTextField directoryField, Game game)
	{
		JButton browseButton = new JButton("Browse");

		browseButton.addActionListener(event -> {
			JFileChooser fc = new JFileChooser(directoryField.getText());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int val = fc.showOpenDialog(null);
			if (val == JFileChooser.APPROVE_OPTION)
			{
				File selectedDir = fc.getSelectedFile();
				if (selectedDir == null)
					return;
				File[] files = selectedDir.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].getName().equalsIgnoreCase(game.getSaveName()))
					{
						directoryField.setText(selectedDir.getPath());
						game.setDirectory(selectedDir);
						OrganizerManager.saveProperties(game);
						return;
					}
				}
				JOptionPane.showMessageDialog(null, "This directory does not contain '" + game.getSaveName() + "'!", "Error occured",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		return browseButton;
	}

}
