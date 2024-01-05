package com.soulsspeedruns.organizer.update;


import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import com.soulsspeedruns.organizer.data.OrganizerManager;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * NewReleasePanel.
 * <p>
 * Panel containing the info about a new release.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 19 Jul 2017
 */
public class NewReleasePanel extends JPanel
{

	/**
	 * Creates a NewReleasePanel.
	 */
	protected NewReleasePanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel newReleaseLabel = new JLabel("New Release Version:");
		JLabel versionLabel = new JLabel(OrganizerManager.getLatestReleaseVersion());
		JLabel changelogLabel = new JLabel("Changelog:");

		String description = OrganizerManager.getLatestReleaseDescription().replaceAll("\\R", "<br>");

		JLabel newReleaseDescriptionLabel = new JLabel(String.format("<html><div WIDTH=%d>%s</div></html>", 200, description));

		newReleaseLabel.setFont(getFont().deriveFont(Font.BOLD));
		changelogLabel.setFont(getFont().deriveFont(Font.BOLD));

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(newReleaseLabel).addComponent(changelogLabel)
				.addComponent(newReleaseDescriptionLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(versionLabel));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(newReleaseLabel).addComponent(versionLabel));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(changelogLabel));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(newReleaseDescriptionLabel));

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}

}
