package com.soulsspeedruns.organizer.main.config;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class SortingComboBoxRenderer implements ListCellRenderer<SortingCategory>
{
	
	private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends SortingCategory> list, SortingCategory category,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, category, index, isSelected, cellHasFocus);
		if (category != null)
			label.setText(category.getCaption());
		return label;
	}

}
