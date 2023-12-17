package com.soulsspeedruns.organizer.about;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.github.pervoj.jiconfont.FontAwesomeBrands;
import com.soulsspeedruns.organizer.data.OrganizerManager;

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

	private static final long serialVersionUID = 1295107948615705937L;


	/**
	 * Creates a new AboutPanel.
	 */
	protected AboutPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel versionLabel = new JLabel("Version:");
		versionLabel.setFont(getFont().deriveFont(Font.BOLD));
		JLabel versionNumberLabel = new JLabel(OrganizerManager.VERSION);

		JLabel developerLabel = new JLabel("Developed by:");
		developerLabel.setFont(getFont().deriveFont(Font.BOLD));
		JLabel developerLink = createDevLink();
		JLabel githubLink = createGitHubLink();
		JLabel discordLabel = createDiscordLabel();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(versionLabel).addComponent(versionNumberLabel)
				.addComponent(developerLabel).addComponent(discordLabel).addComponent(developerLink).addComponent(githubLink));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addComponent(versionLabel).addComponent(versionNumberLabel).addComponent(developerLabel).addComponent(discordLabel).addComponent(developerLink)
				.addComponent(githubLink);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}


	private JLabel createDevLink()
	{
		JLabel developerNameLabel = new JLabel("<html><body><a href=\"\">Kahmul78</a></body></html>");
		developerNameLabel.setIcon(IconFontSwing.buildIcon(Elusive.TWITTER, 20, new Color(64, 153, 255)));
		developerNameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		developerNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		developerNameLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(OrganizerManager.TWITTER_URL));
				}
				catch (Exception ex)
				{
				}
			}
		});
		return developerNameLabel;
	}


	private JLabel createGitHubLink()
	{
		JLabel githubLabel = new JLabel("<html><body><a href=\"\">GitHub Repository</a></body></html>");
		githubLabel.setIcon(IconFontSwing.buildIcon(Elusive.GITHUB, 20));
		githubLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		githubLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		githubLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(OrganizerManager.GITHUB_REPO_URL));
				}
				catch (Exception ex)
				{
				}
			}
		});
		return githubLabel;
	}
	
	private JLabel createDiscordLabel()
	{
		JLabel discordLabel = new JLabel("Kahmul");
		discordLabel.setIcon(IconFontSwing.buildIcon(FontAwesomeBrands.DISCORD, 40, Color.decode("0x5865F2")));
		
		return discordLabel;
	}

}
