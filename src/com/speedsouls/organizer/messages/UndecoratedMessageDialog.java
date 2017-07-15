package com.speedsouls.organizer.messages;


import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;

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
	public UndecoratedMessageDialog(AbstractMessage message)
	{
		super(OrganizerManager.getMainWindow());
		this.message = message;
		Window parentWindow = OrganizerManager.getMainWindow();
		setSize(parentWindow.getWidth() / 3, parentWindow.getHeight() / 10);
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));

		add(message);

		WindowListener wl = new WindowAdapter() {

			public void windowDeactivated(WindowEvent e)
			{
				if (isVisible())
					OrganizerManager.getMainWindow().displayMessageDialog(null);
			}
		};

		addWindowListener(wl);
		addMouseListener(this);
	}


	public void fadeOut()
	{
		message.fadeOut();
	}


	public void fadeIn()
	{
		setVisible(true);
		message.fadeIn();
		Timer timeOut = new Timer();
		timeOut.schedule(new TimerTask() {

			@Override
			public void run()
			{
				if (isVisible())
					OrganizerManager.getMainWindow().displayMessageDialog(null);
			}
		}, 3000);
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
		if (isVisible())
			OrganizerManager.getMainWindow().displayMessageDialog(null);
	}


	@Override
	public void mouseReleased(MouseEvent e)
	{

	}
}
