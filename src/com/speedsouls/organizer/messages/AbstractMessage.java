package com.speedsouls.organizer.messages;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

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


	public AbstractMessage()
	{
		setBackground(new Color(0, 0, 0, 0));
	}


	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(getColor());
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setFont(FONT);
		drawIcon(g);
		drawMessage(g);
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
