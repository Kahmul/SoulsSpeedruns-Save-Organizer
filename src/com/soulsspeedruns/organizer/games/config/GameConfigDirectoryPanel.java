package com.soulsspeedruns.organizer.games.config;


import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.github.weisj.darklaf.ui.text.DarkTextUI;
import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;


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

		File saveFile = game.getSaveFileLocation();
		File gameDir = game.getDirectory();

		JLabel saveFileLabel = new JLabel("Savefile Location:");
		JLabel directoryLabel = new JLabel("Profiles Directory:");

		JTextField saveFileField = createSaveFileTextField(saveFile, game);
		JTextField directoryField = new JTextField(gameDir != null ? gameDir.getPath() : "");

		JButton directoryBrowseButton = createDirectoryBrowseButton(directoryField, game);
		JButton saveFileBrowseButton = createSaveFileBrowseButton(saveFileField, directoryField, directoryBrowseButton, game);

		saveFileField.setEditable(false);
		directoryField.setEditable(false);

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(saveFileLabel).addComponent(saveFileField).addComponent(directoryLabel)
				.addComponent(directoryField));
		hGroup.addGroup(layout.createParallelGroup().addComponent(saveFileBrowseButton).addComponent(directoryBrowseButton));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(saveFileLabel));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(saveFileField).addComponent(saveFileBrowseButton));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(directoryLabel));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(directoryField).addComponent(directoryBrowseButton));

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}


	private JTextField createSaveFileTextField(File saveFile, Game game)
	{
		JTextField saveFileField = new JTextField(saveFile != null ? saveFile.getPath() : "");
		saveFileField.putClientProperty(DarkTextUI.KEY_DEFAULT_TEXT, game.getSuggestedSaveLocation());
		
		return saveFileField;
	}


	/**
	 * @param saveFileField
	 * @param game
	 * @return
	 */
	private JButton createSaveFileBrowseButton(JTextField saveFileField, JTextField directoryField, JButton directoryBrowseButton, Game game)
	{
		JButton browseButton = new JButton("Browse");

		browseButton.addActionListener(event -> {
			JFileChooser fc = new JFileChooser(game.getSaveFilePathOrSuggested());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int val = fc.showOpenDialog(SwingUtilities.windowForComponent(this));
			if (val == JFileChooser.APPROVE_OPTION)
			{
				File selectedSavefile = fc.getSelectedFile();
				if (selectedSavefile == null || !selectedSavefile.exists())
				{
					JOptionPane.showMessageDialog(null, "This file doesn't exist!", "Error occurred", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (selectedSavefile.getName().equalsIgnoreCase(game.getSaveName()))
				{
					game.setSaveFileLocation(selectedSavefile);
					saveFileField.setText(selectedSavefile.getPath());
					directoryBrowseButton.setEnabled(true);
					int confirm = JOptionPane
							.showConfirmDialog(getParent(),
									"Do you wish to use the directory of this savefile to store the profiles for this game?"
											+ " You can choose an alternative directory if you wish.",
									"Choosing Savefile", JOptionPane.YES_NO_OPTION);
					if (confirm == 0)
					{
						directoryField.setText(selectedSavefile.getParentFile().getPath());
						game.setDirectory(selectedSavefile.getParentFile());
						OrganizerManager.saveProperties(game);
						OrganizerManager.fireProfileDirectoryChangedEvent(game);
						return;
					}
					OrganizerManager.saveProperties(game);
					return;
				}
				JOptionPane.showMessageDialog(null, "Filename needs to be '" + game.getSaveName() + "'!", "Error occurred",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		return browseButton;
	}


	/**
	 * Creates the browse button.
	 * 
	 * @param directoryField the directory field of this panel
	 * @param game           the game associated with this panel
	 * @return the browse button
	 */
	private JButton createDirectoryBrowseButton(JTextField directoryField, Game game)
	{
		JButton browseButton = new JButton("Browse");
		browseButton.setEnabled(game.getSaveFileLocation() != null);

		browseButton.addActionListener(event -> {
			if (game.getSaveFileLocation() == null)
			{
				JOptionPane.showMessageDialog(null, "Choose a savefile location first before deciding on a profile directory!", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			JFileChooser fc = new JFileChooser(directoryField.getText());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int val = fc.showOpenDialog(SwingUtilities.windowForComponent(this));
			if (val == JFileChooser.APPROVE_OPTION)
			{
				File selectedDir = fc.getSelectedFile();
				if (selectedDir == null || !selectedDir.exists())
				{
					JOptionPane.showMessageDialog(null, "This directory doesn't exist!", "Error occurred", JOptionPane.ERROR_MESSAGE);
					return;
				}
				directoryField.setText(selectedDir.getPath());
				game.setDirectory(selectedDir);
				OrganizerManager.saveProperties(game);
				OrganizerManager.fireProfileDirectoryChangedEvent(game);
			}
		});
		return browseButton;
	}

}
