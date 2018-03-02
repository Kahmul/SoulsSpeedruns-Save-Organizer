package com.speedsouls.organizer.messages;


import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import com.speedsouls.organizer.data.OrganizerManager;


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

	private static final long serialVersionUID = 2647400160686831681L;

	private AbstractMessage message;


	/**
	 * @param message the message to be shown
	 */
	protected UndecoratedMessageDialog(AbstractMessage message)
	{
		super(OrganizerManager.getMainWindow());

		this.message = message;
		Window parentWindow = OrganizerManager.getMainWindow();
		setSize(parentWindow.getWidth() / 2, parentWindow.getHeight() / 10);
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setUndecorated(true);
		setAutoRequestFocus(false);
		setBackground(new Color(0, 0, 0, 0));
		add(message);

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
