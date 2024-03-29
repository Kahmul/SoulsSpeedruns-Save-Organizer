package com.soulsspeedruns.organizer.messages;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import com.soulsspeedruns.organizer.managers.OrganizerManager;


/**
 * UndecoratedMessageDialog.
 * <p>
 * The undecorated dialog containing a given message panel, e.g. a successful load message.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jul 2017
 */
public class UndecoratedMessageDialog extends JDialog implements MouseListener
{

	private AbstractMessage message;


	/**
	 * @param message the message to be shown
	 */
	protected UndecoratedMessageDialog(AbstractMessage message)
	{
		super(OrganizerManager.getMainWindow());

		this.message = message;
		Window parentWindow = OrganizerManager.getMainWindow();
		setSize(Math.min(400, parentWindow.getWidth() / 2), Math.min(100, parentWindow.getHeight() / 10));
		setLocationRelativeTo(parentWindow);
		setUndecorated(true);
		setAutoRequestFocus(false);
		setBackground(new Color(0, 0, 0, 0));
		setLayout(new BorderLayout());
		add(message, BorderLayout.CENTER);

		WindowListener wl = new WindowAdapter() {

			public void windowDeactivated(WindowEvent e)
			{
				message.fadeOut();
			}
		};

		addWindowListener(wl);
		addMouseListener(this);
	}
	

	/**
	 * Starts the fade-in of the message.
	 */
	protected void fadeIn()
	{
		setVisible(true);
		message.fadeIn();
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
	}


	@Override
	public void mouseEntered(MouseEvent e)
	{

	}


	@Override
	public void mouseExited(MouseEvent e)
	{
	}


	@Override
	public void mousePressed(MouseEvent e)
	{
		message.fadeOut();
	}


	@Override
	public void mouseReleased(MouseEvent e)
	{

	}
	
}
