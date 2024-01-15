package com.soulsspeedruns.organizer.games.config;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.soulsspeedruns.organizer.games.Game;


/**
 * Game List Entry
 * <p>
 * An entry in a GameList instance.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 14 Jan 2024
 */
public class GameListEntry extends JButton implements Transferable
{

	public static final DataFlavor ENTRY_FLAVOR = new DataFlavor(GameListEntry.class, GameListEntry.class.getSimpleName());

	private final Game game;
	private final GameConfigPanel configPanel;

	private final Color backgroundColor = new Color(UIManager.getColor("background").getRGB());
	private final Color backgroundHoverColor = new Color(UIManager.getColor("backgroundHover").getRGB());

	private final Color dropTargetColor = new Color(UIManager.getColor("dropForeground").getRGB());

	private boolean isSelected = false;

	private boolean isDropTarget = false;
	private boolean drawDropTargetBelow = false;

	private GameList list;


	public GameListEntry(Game game, GameList list)
	{
		String caption = game.getCaption();
		if (caption.length() > 22)
		{
			caption = caption.substring(0, 21);
			caption += "...";
		}

		setText(caption);

		this.game = game;
		this.list = list;
		configPanel = new GameConfigPanel(game);

		setFocusPainted(false);
		setBackground(backgroundColor);
		setMargin(new Insets(7, 12, 7, 150));
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				e.consume();
				list.setSelectedEntry(GameListEntry.this);
			}
		});

		new GameListEntryDragListener(this, list);
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


	/**
	 * Sets whether this entry is highlighted as a potential drop location in the list.
	 * 
	 * @param isDropTarget whether to highlight this entry as a drop location
	 * @param drawDropTargetBelow whether to draw the drop line below or above the entry
	 */
	public void setIsDropTarget(boolean isDropTarget, boolean drawDropTargetBelow)
	{
		if(this.isDropTarget == isDropTarget && this.drawDropTargetBelow == drawDropTargetBelow)
			return;
		this.isDropTarget = isDropTarget;
		this.drawDropTargetBelow = drawDropTargetBelow;
		repaint();
	}
	
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		if(isDropTarget)
		{
			g.setColor(dropTargetColor);
			if(drawDropTargetBelow)
			{
				g.fillRect(0, getHeight() - 2, getWidth() - 1, 2);
				return;
			}
			g.fillRect(0, 0, getWidth() - 1, 2);
		}
	}


	public Game getGame()
	{
		return game;
	}


	public GameConfigPanel getConfigPanel()
	{
		return configPanel;
	}


	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { ENTRY_FLAVOR };
	}


	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(ENTRY_FLAVOR);
	}


	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (isDataFlavorSupported(flavor))
			return list.getIndexOfEntry(this);
		throw new UnsupportedFlavorException(flavor);
	}
}
