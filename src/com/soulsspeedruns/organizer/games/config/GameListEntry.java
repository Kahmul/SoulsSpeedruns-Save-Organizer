package com.soulsspeedruns.organizer.games.config;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.soulsspeedruns.organizer.games.Game;

public class GameListEntry extends JButton
{

	private final Game game;
	private final GameConfigPanel configPanel;

	private final Color backgroundColor = new Color(UIManager.getColor("background").getRGB());
	private final Color backgroundHoverColor = new Color(UIManager.getColor("backgroundHover").getRGB());
	
//	private final Color dropTargetColor = new Color(UIManager.getColor("hyperlink").getRGB());

	private boolean isSelected = false;


	public GameListEntry(Game game, ScrollableGamesConfigPane pane)
	{
		super(game.getCaption());

		this.game = game;
		configPanel = new GameConfigPanel(game);

		setFocusPainted(false);
		setBackground(backgroundColor);
		setMargin(new Insets(7, 12, 7, 150));
		setBorder(new EmptyBorder(0, 0, 0, 0));
//		setBorder(new MatteBorder(0, 0, 1, 0, dropTargetColor));

		addActionListener((e) -> {
			pane.setSelectedEntry(this);
		});
	}


	public boolean isSelected()
	{
		return isSelected;
	}


	public void setSelected(boolean flag)
	{
		isSelected = flag;
		setBackground(flag ? backgroundHoverColor : backgroundColor);
	}


	public Game getGame()
	{
		return game;
	}


	public GameConfigPanel getConfigPanel()
	{
		return configPanel;
	}
}
