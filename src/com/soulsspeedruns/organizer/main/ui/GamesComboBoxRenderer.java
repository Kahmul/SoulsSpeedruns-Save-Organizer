package com.soulsspeedruns.organizer.main.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.soulsspeedruns.organizer.games.Game;

public class GamesComboBoxRenderer implements ListCellRenderer<Game>
{
	
	private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends Game> list, Game game, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, game, index, isSelected, cellHasFocus);
		if (game != null)
			label.setText(game.getCaption());
		return label;
	}

}
