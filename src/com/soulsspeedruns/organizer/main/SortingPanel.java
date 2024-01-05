package com.soulsspeedruns.organizer.main;


import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import com.soulsspeedruns.organizer.mainconfig.SearchBar;
import com.soulsspeedruns.organizer.mainconfig.SortingComboBox;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Sorting segment of the main window.
 * <p>
 * Contains the 'Sort by' combobox and the searchbar.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 18 May 2016
 */
public class SortingPanel extends JPanel
{

	private static final long serialVersionUID = -9085347834231860908L;


	protected SortingPanel()
	{
		GroupLayout layout = new GroupLayout(this);

		JLabel sortByLabel = new JLabel("Sort by:");
		SortingComboBox sortingComboBox = new SortingComboBox();
		sortingComboBox
				.setPreferredSize(new Dimension(sortingComboBox.getPreferredSize().width + 2, sortingComboBox.getPreferredSize().height));
		SearchBar searchBar = new SearchBar();

		Component glue = Box.createHorizontalGlue();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGap(10);
		hGroup.addGroup(layout.createParallelGroup().addComponent(sortByLabel));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup().addComponent(sortingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
				GroupLayout.PREFERRED_SIZE));
		hGroup.addGap(6);
		hGroup.addGroup(layout.createParallelGroup().addComponent(searchBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE));
		hGroup.addGap(10);
		hGroup.addGroup(layout.createParallelGroup().addComponent(glue));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(sortByLabel)
				.addComponent(sortingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(searchBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(glue));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}

}
