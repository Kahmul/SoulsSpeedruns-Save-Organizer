package com.soulsspeedruns.organizer.mainconfig;


import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * SortingComboBox
 * <p>
 * ComboBox displaying the sorting options.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 18 May 2016
 */
public class SortingComboBox extends JComboBox<SortingCategory>
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

		setRenderer(new SortingComboBoxRenderer());
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				OrganizerManager.setSelectedSortingCategory((SortingCategory) event.getItem());
			}
		});
	}

}
