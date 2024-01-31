package com.soulsspeedruns.organizer.main.ui;


import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

import com.soulsspeedruns.organizer.managers.SavesManager;


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

	/**
	 * Creates a new sorting combobox.
	 */
	public SortingComboBox()
	{
		for (SortingCategory category : SortingCategory.values())
		{
			addItem(category);
			if (category == SavesManager.getSelectedSortingCategory())
				setSelectedItem(category);
		}

		setRenderer(new SortingComboBoxRenderer());
		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED)
			{
				SavesManager.setSelectedSortingCategory((SortingCategory) event.getItem());
			}
		});
	}

}
