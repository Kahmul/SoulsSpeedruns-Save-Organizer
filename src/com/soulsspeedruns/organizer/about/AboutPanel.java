package com.soulsspeedruns.organizer.about;


import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.mainconfig.HyperLink;

import jiconfont.icons.Elusive;
import jiconfont.swing.IconFontSwing;


/**
 * AboutPanel.
 * <p>
 * Panel containing information about the program.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 5 Jun 2016
 */
public class AboutPanel extends JPanel
{

	/**
	 * Creates a new AboutPanel.
	 */
	protected AboutPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel versionLabel = new JLabel("<html><b>Version:</b></html>");
		versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel versionNumberLabel = new JLabel(OrganizerManager.VERSION);

		JLabel developerLabel = new JLabel("<html><b>Developed by:</b></html>");
		developerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel discordLabel = createDiscordLabel();
		HyperLink devLink = createDevLink();
		HyperLink githubLink = createGitHubLink();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(versionLabel).addComponent(versionNumberLabel)
				.addComponent(developerLabel).addComponent(discordLabel).addComponent(devLink).addComponent(githubLink));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addComponent(versionLabel).addComponent(versionNumberLabel).addComponent(developerLabel).addComponent(discordLabel)
				.addComponent(devLink).addComponent(githubLink);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}
	
	
	private JLabel createDiscordLabel()
	{
		JLabel discordLabel = new JLabel("Kahmul");
		discordLabel.setIcon(OrganizerManager.discordIcon);
		
		return discordLabel;
	}
	
	
	private HyperLink createDevLink()
	{
		HyperLink devLink = new HyperLink("Kahmul78", OrganizerManager.TWITTER_URL);
		devLink.setHorizontalAlignment(SwingConstants.RIGHT);
		devLink.setIcon(IconFontSwing.buildIcon(Elusive.TWITTER, 20, new Color(64, 153, 255)));
		
		return devLink;
	}
	
	
	private HyperLink createGitHubLink()
	{
		HyperLink githubLink = new HyperLink("GitHub Repository", OrganizerManager.GITHUB_REPO_URL);
		githubLink.setHorizontalAlignment(SwingConstants.RIGHT);
		githubLink.setIcon(IconFontSwing.buildIcon(Elusive.GITHUB, 20));
		
		return githubLink;
	}

}
