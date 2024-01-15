package com.soulsspeedruns.organizer.games.config;


import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;


public class GameListEntryDragListener implements DragSourceListener, DragGestureListener
{

	private GameListEntry entry;
	private DragSource dragSource;
	private GameList list;


	public GameListEntryDragListener(GameListEntry entry, GameList list)
	{
		this.entry = entry;
		this.list = list;

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_MOVE, this);
	}


	@Override
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		dragSource.startDrag(dge, null, entry, this);
	}


	@Override
	public void dragEnter(DragSourceDragEvent dsde)
	{
	}


	@Override
	public void dragOver(DragSourceDragEvent dsde)
	{
	}


	@Override
	public void dropActionChanged(DragSourceDragEvent dsde)
	{
	}


	@Override
	public void dragExit(DragSourceEvent dse)
	{
	}


	@Override
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
		list.setDropTargetEntry(null);
	}

}
