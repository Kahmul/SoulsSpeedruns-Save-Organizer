package com.soulsspeedruns.organizer.about;


import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * AboutWindow.
 * <p>
 * Small window showing links to Twitter and GitHub, as well as the current version.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Jun 2016
 */
public class AboutWindow extends JDialog
{

	private static final long serialVersionUID = 3660600135211221410L;


	/**
	 * Creates a new AboutWindow.
	 */
	public AboutWindow()
	{
		super(null, "About SoulsSpeedruns - Save Organizer", Dialog.ModalityType.APPLICATION_MODAL);

		initLayout();
		initProperties();

		setVisible(true);
	}


	/**
	 * 
	 */
	private void initProperties()
	{
		pack();
		setResizable(false);
		setIconImage(OrganizerManager.soulsspeedrunsIcon);
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());
	}


	/**
	 * 
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 50, 0));
		guiPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel iconLabel = createIconLabel();
		
		guiPanel.add(new AboutPanel());
		guiPanel.add(iconLabel);

		add(guiPanel);
	}
	
	private JLabel createIconLabel()
	{
		JLabel iconLabel = new JLabel(new ImageIcon(OrganizerManager.soulsspeedrunsIconMedium));
		iconLabel.addMouseListener(new MouseListener() {
			
			int count = 0;
			boolean once = false;
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				count++;
				if(count >= 3 && !once)
				{
					once = true;
					iconLabel.setIcon(OrganizerManager.frankerZIcon);
					vibrate();
				}
			}
		});
		
		return iconLabel;
	}
	
	 private void vibrate() { 
	    try
	    { 
			int originalX = getLocationOnScreen().x; 
			int originalY = getLocationOnScreen().y; 
			for(int i = 0; i < 5; i++) { 
				Thread.sleep(10); 
				setLocation(originalX, originalY + 5); 
				Thread.sleep(10); 
				setLocation(originalX, originalY - 5);
				Thread.sleep(10); 
				setLocation(originalX + 5, originalY);
				Thread.sleep(10); 
				setLocation(originalX, originalY); 
			} 
	    } 
	    catch (Exception err)
	    { 
	    	err.printStackTrace(); 
	    }
	  }
}
