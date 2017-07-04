package com.speedsouls.organizer.main;


import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.speedsouls.organizer.components.SaveList;


/**
 * List Panel.
 * <p>
 * Contains the list with savestates.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class ListPanel extends JPanel
{

	private static final long serialVersionUID = 6198586503518113347L;


	/**
	 * Creates a new ListPanel for the main window.
	 */
	protected ListPanel()
	{
		GroupLayout layout = new GroupLayout(this);

		SaveList saveList = new SaveList();

		JScrollPane savePane = new JScrollPane(saveList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGap(10);
		hGroup.addGroup(layout.createParallelGroup().addComponent(savePane));
		hGroup.addGap(10);

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(savePane));

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}

}
