package com.soulsspeedruns.organizer.savelist;


import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * SortingComboBox
 * <p>
 * ComboBox displaying the sorting options.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 18 May 2016
 */
public class SortingComboBox extends JComboBox<SortingCategory> implements ListCellRenderer<SortingCategory>
{

	private static final long serialVersionUID = -8190201929116747754L;


	/**
	 * Creates a new sorting combobox.
	 */
	public SortingComboBox()
	{
		for (SortingCategory category : SortingCategory.values())
		{
			addItem(category);
			if (category == OrganizerManager.getSelectedSortingCategory())
				setSelectedItem(category);
		}

		setRenderer(this);
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				OrganizerManager.setSelectedSortingCategory((SortingCategory) event.getItem());
			}
		});
	}


	@Override
	public Component getListCellRendererComponent(JList<? extends SortingCategory> list, SortingCategory category, int index,
			boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) new DefaultListCellRenderer().getListCellRendererComponent(list, category, index, isSelected, cellHasFocus);
		if (category != null)
			label.setText(category.getCaption());
		return label;
	}
}
