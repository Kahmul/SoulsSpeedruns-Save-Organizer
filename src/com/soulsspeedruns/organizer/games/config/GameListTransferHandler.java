package com.soulsspeedruns.organizer.games.config;


import javax.swing.TransferHandler;


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
		DropLocation dl = support.getDropLocation();
		
		GameListEntry dropTarget = list.getEntryByPoint(dl.getDropPoint());
		try
		{
//			GameListEntry entry = (GameListEntry) support.getTransferable().getTransferData(GameListEntry.ENTRY_FLAVOR);
//			if(dropTarget != entry)
				list.setDropTargetEntry(dropTarget);
		}
		catch (Exception e)
		{
			return false;
		}
		
//		System.out.println(dropTarget.getGame().getCaption());

//		int index = saveList.locationToIndex(dl.getDropPoint());
//		SwingUtilities.conve
//		if (index == -1)
//			return false;
//		try
//		{
//			GameListEntry entry = (GameListEntry) support.getTransferable().getTransferData(GameListEntry.ENTRY_FLAVOR);
//			support.setShowDropLocation(true);
//		}
//		catch (Exception e)
//		{
//			return false;
//		}
		return true;
	}
//
//
//	@Override
//	public boolean importData(TransferHandler.TransferSupport support)
//	{
//		if (!canImport(support))
//			return false;
//		try
//		{
//			SaveListEntry entry = (SaveListEntry) support.getTransferable().getTransferData(SaveListEntry.ENTRY_FLAVOR);
//
//			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
//			Folder newParentFolder = findNewParentFolderFromDropLocation(dl);
//
//			Path newPath = Paths.get(newParentFolder.getFile().getPath()).resolve(entry.getName());
//			if (newPath.toFile().exists())
//			{
//				if (JOptionPane.showConfirmDialog(saveList.getParent(),
//						entry.getName() + " already exists in that directory. Do you want to overwrite?", "Confirmation",
//						JOptionPane.YES_NO_OPTION) != 0)
//					return false;
//				SaveListEntry existingEntry = newParentFolder.getChildByName(entry.getName());
//				((DefaultListModel<SaveListEntry>) saveList.getModel()).removeElement(existingEntry);
//				existingEntry.delete();
//			}
//			
//			entry.moveToNewParent(newParentFolder);
//			newParentFolder.setClosed(false);
//			saveList.refreshList();
//			saveList.setSelectedValue(entry, true);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

}
