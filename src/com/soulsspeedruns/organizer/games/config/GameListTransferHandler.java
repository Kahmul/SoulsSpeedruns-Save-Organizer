package com.soulsspeedruns.organizer.games.config;


import javax.swing.TransferHandler;


/**
 * Game List Transfer Handler
 * <p>
 * A TransferHandler for GameList.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jan 2024
 */
public class GameListTransferHandler extends TransferHandler
{

	private GameList list;


	public GameListTransferHandler(GameList list)
	{
		this.list = list;
	}


	@Override
	public boolean canImport(TransferHandler.TransferSupport support)
	{
		if (!support.isDataFlavorSupported(GameListEntry.ENTRY_FLAVOR))
			return false;
		try
		{
			int draggedIndex = (int) support.getTransferable().getTransferData(GameListEntry.ENTRY_FLAVOR);

			DropLocation dl = support.getDropLocation();
			int dropIndex = list.getDropIndexByPoint(dl.getDropPoint());

			if (isValidDropIndex(draggedIndex, dropIndex))
			{
				list.setDropTargetIndex(dropIndex);
				return true;
			}

			list.clearDropTarget();
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * Returns whether the given drop index is valid given the index of the dragged entry.
	 * 
	 * @param draggedIndex the index of the dragged entry
	 * @param dropIndex the index at which to drop the dragged entry
	 * @return whether the drop index is valid
	 */
	private boolean isValidDropIndex(int draggedIndex, int dropIndex)
	{
		if (dropIndex > draggedIndex)
			return dropIndex - draggedIndex > 1;

		return draggedIndex - dropIndex > 0;
	}


	@Override
	public boolean importData(TransferHandler.TransferSupport support)
	{
		if (!canImport(support))
			return false;
		try
		{
			int draggedIndex = (int) support.getTransferable().getTransferData(GameListEntry.ENTRY_FLAVOR);

			DropLocation dl = support.getDropLocation();
			int dropIndex = list.getDropIndexByPoint(dl.getDropPoint());
			
			list.moveIndexToNewIndex(draggedIndex, dropIndex);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
