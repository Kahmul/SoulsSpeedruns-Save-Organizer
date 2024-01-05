package com.soulsspeedruns.organizer.mainconfig;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.event.ThemeChangeEvent;
import com.github.weisj.darklaf.theme.event.ThemeChangeListener;

public class HyperLink extends JButton
{

	public HyperLink(String text, String url)
    {
		super(text);
		
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		setMargin(new Insets(0, 0, 0, 0));
		
		setToolTipText(url);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		setForeground((Color) UIManager.get("hyperlink"));
		
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(url));
				}
				catch (Exception ex)
				{
				}
			}
		});
		
		LafManager.addThemeChangeListener(new ThemeChangeListener() {
			
			@Override
			public void themeInstalled(ThemeChangeEvent e)
			{
				setForeground((Color) UIManager.get("hyperlink"));
			}
			
			@Override
			public void themeChanged(ThemeChangeEvent e)
			{
			}
		});
	}

	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		FontMetrics fm = g.getFontMetrics();
		
        if(getModel().isRollover())
        {
        	Icon icon = getIcon();
        	if(icon != null)
            	g.drawLine(icon.getIconWidth() + getIconTextGap() + 2, fm.getHeight() + fm.getDescent(), getWidth() - 2, fm.getHeight() + fm.getDescent());
        	else
        		g.drawLine(2, fm.getHeight(), getWidth() - 2, fm.getHeight());
        }

	}
	
}
