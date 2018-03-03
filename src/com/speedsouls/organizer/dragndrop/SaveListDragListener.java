package com.speedsouls.organizer.dragndrop;


import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import com.speedsouls.organizer.savelist.SaveList;


/**
 * SaveListDragListener
 * <p>
 * DragListener for SaveList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 May 2016
 */
public class SaveListDragListener implements DragSourceListener, DragGestureListener
{

	private SaveList saveList;
	private DragSource dragSource;


	/**
	 * Creates a new drag listener for a SaveList.
	 */
	public SaveListDragListener(SaveList saveList)
	{
		this.saveList = saveList;
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(saveList, DnDConstants.ACTION_MOVE, this);
	}


	@Override
	public void dragGestureRecognized(DragGestureEvent e)
	{
		if (saveList.getSelectedValue() != null)
			dragSource.startDrag(e, null, saveList.getSelectedValue(), this);
	}


	@Override
	public void dragDropEnd(DragSourceDropEvent e)
	{
	}


	@Override
	public void dragEnter(DragSourceDragEvent e)
	{
	}


	@Override
	public void dragExit(DragSourceEvent e)
	{
	}


	@Override
	public void dragOver(DragSourceDragEvent e)
	{
	}


	@Override
	public void dropActionChanged(DragSourceDragEvent e)
	{
	}

}
