package com.speedsouls.organizer.messages;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JPanel;

import jiconfont.IconCode;
import jiconfont.swing.IconFontSwing;


/**
 * AbstractMessage.
 * <p>
 * The abstract superclass for all message implementations.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jul 2017
 */
public abstract class AbstractMessage extends JPanel
{

	private static final long serialVersionUID = -7470202821259477366L;

	private static final Font FONT = new Font("Calibri", Font.BOLD, 17);
	private static final int TIME_OUT = 2500;

	public static final AbstractMessage SUCCESSFUL_DELETE = new SuccessfulDeleteMessage();
	public static final AbstractMessage SUCCESSFUL_IMPORT = new SuccessfulImportMessage();
	public static final AbstractMessage SUCCESSFUL_LOAD = new SuccessfulLoadMessage();
	public static final AbstractMessage SUCCESSFUL_REPLACE = new SuccessfulReplaceMessage();
	public static final AbstractMessage SUCCESSFUL_REFRESH = new SuccessfulRefreshMessage();
	public static final AbstractMessage SUCCESSFUL_COPY = new SuccessfulCopyMessage();
	public static final AbstractMessage SUCCESSFUL_PASTE = new SuccessfulPasteMessage();

	// used to prevent fadeout when redisplaying a currently displayed message
	private static AbstractMessage currentMessage;

	private UndecoratedMessageDialog dialog = null;

	private float alpha = 0.0f;
	private float fadeInOutRate = 0.15f;

	private boolean fadingOut = false;

	private Timer fadeOutTimer = new Timer();


	protected AbstractMessage()
	{
		setBackground(new Color(0, 0, 0, 0));
	}


	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// set the opacity
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(getColor());
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setFont(FONT);
		drawIcon(g);
		drawMessage(g);
		fadeInOut();
	}


	/**
	 * Adjusts the alpha value depending on whether the message is in fade-in or fade-out mode.
	 */
	private void fadeInOut()
	{
		if (fadingOut)
		{
			alpha -= fadeInOutRate;
			if (alpha <= 0.0f)
			{
				alpha = 0.0f;
				dialog.setVisible(false);
				fadingOut = false;
				return;
			}
			repaint();
			return;
		}
		alpha += fadeInOutRate;
		if (alpha >= 1.0f)
		{
			alpha = 1.0f;
			return;
		}
		repaint();
	}


	/**
	 * Draws the icon associated with this message.
	 * 
	 * @param g the graphics to draw on
	 */
	private void drawIcon(Graphics g)
	{
			Icon icon = IconFontSwing.buildIcon(getIcon(), 30, getColor());
			icon.paintIcon(null, g, getWidth() / 6 - icon.getIconWidth() / 2, getHeight() / 2 - icon.getIconHeight() / 2);
	}


	/**
	 * Draws the text associated with this message.
	 * 
	 * @param g the graphics to draw on
	 */
	private void drawMessage(Graphics g)
	{
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = getWidth() / 2 - metrics.stringWidth(getMessage()) / 2;
		int y = getHeight() / 2 - metrics.getHeight() / 2 + metrics.getAscent();
		g.drawString(getMessage(), x, y);
	}


	/**
	 * Starts the fade-out. Is the fade-out complete, the parent dialog is set invisible.
	 */
	protected void fadeOut()
	{
		// don't fadeout the current message
		if (this.equals(currentMessage))
			return;
		fadingOut = true;
		repaint();
	}


	/**
	 * Starts the fade-in.
	 */
	protected void fadeIn()
	{
		fadingOut = false;
		repaint();
	}


	/**
	 * Starts the fade-in of the message, stops any ongoing fade-out, and resets the fade-out timer.
	 */
	private void display()
	{
		fadingOut = false;
		fadeOutTimer.cancel();
		fadeOutTimer = new Timer();
		fadeOutTimer.schedule(new TimerTask() {

			@Override
			public void run()
			{
				fadingOut = true;
				repaint();
			}
		}, TIME_OUT);
		if (alpha > 0.0f)
			return;
		dialog = new UndecoratedMessageDialog(this);
		dialog.fadeIn();
	}


	/**
	 * Display the given message on screen.
	 * 
	 * @param message the message to display
	 */
	public static void display(AbstractMessage message)
	{
		currentMessage = message;
		SUCCESSFUL_DELETE.fadeOut();
		SUCCESSFUL_IMPORT.fadeOut();
		SUCCESSFUL_LOAD.fadeOut();
		SUCCESSFUL_REPLACE.fadeOut();
		message.display();
	}


	/**
	 * Clears all displayed messages on the screen.
	 */
	public static void clearAllMessages()
	{
		currentMessage = null;
		SUCCESSFUL_DELETE.fadeOut();
		SUCCESSFUL_IMPORT.fadeOut();
		SUCCESSFUL_LOAD.fadeOut();
		SUCCESSFUL_REPLACE.fadeOut();
	}


	/**
	 * The text associated with this message.
	 * 
	 * @return the text
	 */
	protected abstract String getMessage();


	/**
	 * The icon associated with this message.
	 * 
	 * @return the icon
	 */
	protected abstract IconCode getIcon();


	/**
	 * The color associated with this message.
	 * 
	 * @return the color
	 */
	protected abstract Color getColor();

}
