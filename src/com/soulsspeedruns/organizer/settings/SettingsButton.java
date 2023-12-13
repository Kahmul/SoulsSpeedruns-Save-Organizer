package com.soulsspeedruns.organizer.settings;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * SettingsButton.
 * <p>
 * Opens a context menu for settings changes and other things.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 May 2016
 */
public class SettingsButton extends JLabel implements MouseListener
{

	private static final long serialVersionUID = -3605228058130886152L;

	private boolean highlighted = false;
	private boolean pressed = false;


	/**
	 * Creates a new SettingsButton.
	 */
	public SettingsButton()
	{
		super(new ImageIcon(OrganizerManager.settingsIcon));
		addMouseListener(this);
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		if (highlighted || pressed)
		{
			g.setColor(new Color(0, 0, 182, 30));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(new Color(0, 0, 220, 70));
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		super.paintComponent(g);
	}


	@Override
	public void mouseClicked(MouseEvent e)
	{
		new SettingsContextMenu(this);
	}


	@Override
	public void mouseEntered(MouseEvent e)
	{
		highlighted = true;
		repaint();
	}


	@Override
	public void mouseExited(MouseEvent e)
	{
		highlighted = false;
		repaint();
	}


	@Override
	public void mousePressed(MouseEvent e)
	{

	}


	@Override
	public void mouseReleased(MouseEvent e)
	{

	}


	/**
	 * Sets the pressed state of this button.
	 * 
	 * @param pressed whether the button should be considered pressed or not
	 */
	public void setIsPressed(boolean pressed)
	{
		this.pressed = pressed;
		repaint();
	}


	/**
	 * Returns whether the button is considered pressed or not.
	 * 
	 * @return whether the button is considered pressed
	 */
	public boolean isPressed()
	{
		return pressed;
	}

}
