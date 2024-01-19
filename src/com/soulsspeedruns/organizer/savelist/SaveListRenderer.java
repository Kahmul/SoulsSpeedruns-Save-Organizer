package com.soulsspeedruns.organizer.savelist;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class SaveListRenderer implements ListCellRenderer<SaveListEntry>
{
	
	private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends SaveListEntry> list, SaveListEntry entry, int index,
			boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, entry, index, isSelected, cellHasFocus);
		entry.render(list, index, label);
		
		return label;
	}

}
