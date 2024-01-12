package com.soulsspeedruns.organizer.main.config;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.soulsspeedruns.organizer.games.Profile;

public class ProfilesComboBoxRenderer implements ListCellRenderer<Profile>
{
	
	private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends Profile> list, Profile profile, int index,
			boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, profile, index, isSelected, cellHasFocus);
		if (profile != null)
			label.setText(profile.getName());
		return label;
	}

}
