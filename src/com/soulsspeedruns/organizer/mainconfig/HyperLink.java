package com.soulsspeedruns.organizer.mainconfig;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		
		setForeground(new Color(UIManager.getColor("hyperlink").getRGB()));
		
		getModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setText(getModel().isRollover() ? "<html><u>" + text + "</u></html>" : text);
			}
		});
		
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
				setForeground(new Color(UIManager.getColor("hyperlink").getRGB()));
			}
			
			@Override
			public void themeChanged(ThemeChangeEvent e)
			{
			}
		});
	}

}
