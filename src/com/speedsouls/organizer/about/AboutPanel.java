package com.speedsouls.organizer.about;


import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.speedsouls.organizer.data.OrganizerManager;


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
		JLabel versionNumberLabel = new JLabel(OrganizerManager.VERSION);

		JLabel developerLabel = new JLabel("Developed by:");
		JLabel developerNameLabel = new JLabel("<html><body><a href=\"\">Kahmul78</a></body></html>");
		developerNameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		developerNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		developerNameLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI("www.twitter.com/Kahmul78"));
				}
				catch (Exception ex)
				{
				}
			}
		});

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(versionLabel).addComponent(versionNumberLabel)
				.addComponent(developerLabel).addComponent(developerNameLabel));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addComponent(versionLabel).addComponent(versionNumberLabel).addComponent(developerLabel).addComponent(developerNameLabel);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}

}
