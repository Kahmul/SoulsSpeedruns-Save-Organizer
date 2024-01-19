package com.soulsspeedruns.organizer.update;


import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;


/**
 * NewReleaseWindow.
 * <p>
 * Window giving info about a new release, if there is any, as well as a link to the download.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 19 Jul 2017
 */
public class NewReleaseWindow extends JDialog
{

	/**
	 * Creates a new AboutWindow.
	 */
	public NewReleaseWindow()
	{
		super(null, "New Release Available!", Dialog.ModalityType.APPLICATION_MODAL);

		initLayout();
		initProperties();

		setVisible(true);
		requestFocus();
	}


	/**
	 * 
	 */
	private void initProperties()
	{
		setResizable(false);
		setIconImage(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_MEDIUM));
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());
	}


	/**
	 * 
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new GridBagLayout());
		guiPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 50, 0));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));

		contentPanel.add(new NewReleasePanel());
		contentPanel.add(new JLabel(new ImageIcon(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_LARGE))));

		GridBagConstraints constraints = new GridBagConstraints();
		guiPanel.add(contentPanel, constraints);
		constraints.gridy = 1;
		guiPanel.add(createNewDownloadButton(), constraints);

		add(guiPanel);
		pack();
	}


	private JButton createNewDownloadButton()
	{
		JButton downloadButton = new JButton("Go to Releases");
		downloadButton.addActionListener(e -> {
			OrganizerManager.openLatestReleasePage();
			setVisible(false);
		});
		downloadButton.setToolTipText(OrganizerManager.LATEST_RELEASE_URL);
		return downloadButton;
	}
}
