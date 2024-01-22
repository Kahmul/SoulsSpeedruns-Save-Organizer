package com.soulsspeedruns.organizer.main;


import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.soulsspeedruns.organizer.savelist.SaveList;


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

	/**
	 * Creates a new ListPanel for the main window.
	 */
	protected ListPanel()
	{
		GroupLayout layout = new GroupLayout(this);

		SaveList saveList = new SaveList();

		OverlayScrollPane savePane = new OverlayScrollPane(saveList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGap(12);
		hGroup.addGroup(layout.createParallelGroup().addComponent(savePane));
		hGroup.addGap(12);

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(savePane));

		layout.setVerticalGroup(vGroup);

		setLayout(layout);
	}

}
