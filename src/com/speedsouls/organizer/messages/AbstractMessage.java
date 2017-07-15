package com.speedsouls.organizer.messages;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JPanel;

import jiconfont.IconCode;
import jiconfont.swing.IconFontSwing;


/**
 * Short description.
 * <p>
 * Long description.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jul 2017
 */
public abstract class AbstractMessage extends JPanel
{

	private static final long serialVersionUID = -7470202821259477366L;

	private static final Font FONT = new Font("Calibri", Font.BOLD, 17);
	private float alpha = 0.0f;
	private float fadeInOutRate = 0.1f;
	private boolean fadingOut = false;


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

		g.setColor(getColor());
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setFont(FONT);
		drawIcon(g);
		drawMessage(g);
		fadeInOut();
	}


	private void fadeInOut()
	{
		if (fadingOut)
		{
			alpha -= fadeInOutRate;
			if (alpha <= 0.0f)
			{
				alpha = 0.0f;
				getParent().setVisible(false);
				return;
			}
			repaint();
		}
		else
		{
			alpha += fadeInOutRate;
			if (alpha >= 1.0f)
			{
				alpha = 1.0f;
				return;
			}
			repaint();
		}
	}


	/**
	 * @param g
	 */
	private void drawIcon(Graphics g)
	{
		Icon icon = IconFontSwing.buildIcon(getIcon(), 30, getColor());
		icon.paintIcon(null, g, getWidth() / 6 - icon.getIconWidth() / 2, getHeight() / 2 - icon.getIconHeight() / 2);
	}


	/**
	 * @param g
	 */
	private void drawMessage(Graphics g)
	{
		FontMetrics metrics = g.getFontMetrics(g.getFont());
		int x = getWidth() / 2 - metrics.stringWidth(getMessage()) / 2;
		int y = getHeight() / 2 - metrics.getHeight() / 2 + metrics.getAscent();
		g.drawString(getMessage(), x, y);
	}


	protected void fadeOut()
	{
		fadingOut = true;
		repaint();
	}


	/**
	 * @return
	 */
	protected abstract String getMessage();


	/**
	 * @return
	 */
	protected abstract IconCode getIcon();


	/**
	 * @return
	 */
	protected abstract Color getColor();

}
