/**
 * 
 */
package com.soulsspeedruns.organizer.components;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;


/**
 * <ShortDescription>
 * <p>
 * <LongDescription>
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Feb 2024
 */
public class ImageBackgroundPanel extends JPanel
{

	private Image background;


	public ImageBackgroundPanel(Image background)
	{
		this.background = background;
	}


	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(new Color(0, 0, 0, 255));
//		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(background, 0, 0, this);
	}


	@Override
	public Dimension preferredSize()
	{
		return new Dimension(background.getWidth(this), background.getHeight(this));
	}

}
