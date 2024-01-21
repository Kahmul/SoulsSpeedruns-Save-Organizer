/**
 * 
 */
package com.soulsspeedruns.organizer.managers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.listeners.NavigationListener;
import com.soulsspeedruns.organizer.listeners.SaveListener;
import com.soulsspeedruns.organizer.listeners.SearchListener;
import com.soulsspeedruns.organizer.listeners.SortingListener;
import com.soulsspeedruns.organizer.main.config.SortingCategory;
import com.soulsspeedruns.organizer.messages.AbstractMessage;
import com.soulsspeedruns.organizer.savelist.Folder;
import com.soulsspeedruns.organizer.savelist.Save;
import com.soulsspeedruns.organizer.savelist.SaveListEntry;


/**
 * SavesManager.
 * <p>
 * Offers methods related to manipulating saves and folders.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 20 Jan 2024
 */
public class SavesManager
{

	private static List<SaveListener> saveListeners;
	private static List<SearchListener> searchListeners;
	private static List<SortingListener> sortingListeners;
	private static List<NavigationListener> navigationListeners;

	private static SaveListEntry selectedEntry;


	protected static void initialize()
	{
		saveListeners = new ArrayList<>();
		searchListeners = new ArrayList<>();
		sortingListeners = new ArrayList<>();
		navigationListeners = new ArrayList<>();
	}


	/**
	 * Imports a new savefile. If a parent is given, it will be imported into that folder. Otherwise the parent will be determined based on the
	 * selection in the save list.
	 * 
	 * @param parentFolder the folder to import the savefile into
	 * @return the imported save
	 */
	public static Save importSavefile(Folder parentFolder)
	{
		SaveListEntry parent = getSelectedEntry();
		if (parent instanceof Save)
			parent = parent.getParent();
		if (parentFolder != null)
			parent = parentFolder;
		if (parent == null)
			parent = GamesManager.getSelectedProfile().getRoot();
		File saveFile = createFileForNewSave((Folder) parent);
		if (saveFile == null)
			return null;
		Save newSave = new Save((Folder) parent, saveFile);
		parent.addChild(newSave);
		AbstractMessage.display(AbstractMessage.SUCCESSFUL_IMPORT);
		fireEntryCreatedEvent(newSave);
		return newSave;
	}


	/**
	 * Imports a new savefile and replaces an existing one in the list.
	 * 
	 * @param saveToReplace the save to be replaced by the imported one
	 */
	public static void importAndReplaceSavefile(Save saveToReplace)
	{
		Folder parent = saveToReplace.getParent();
		String name = saveToReplace.getName();
		saveToReplace.delete();
		File saveFile = createFileForNewSave(parent);
		if (saveFile == null)
			return;
		Save newSave = new Save(parent, saveFile);
		newSave.rename(name);
		parent.addChild(newSave);
		AbstractMessage.display(AbstractMessage.SUCCESSFUL_REPLACE);
		fireEntryCreatedEvent(newSave);
	}


	public static void createFolder(String name)
	{
		SaveListEntry parent = getSelectedEntry();
		if (parent == null)
			parent = GamesManager.getSelectedProfile().getRoot();
		if (parent instanceof Save)
			parent = parent.getParent();
		File dir = new File(parent.getFile().getPath() + File.separator + name);
		if (dir.exists())
		{
			JOptionPane.showMessageDialog(OrganizerManager.getMainWindow(), "This folder already exists!", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		dir.mkdirs();
		Folder newFolder = new Folder((Folder) parent, dir);
		parent.addChild(newFolder);
		fireEntryCreatedEvent(newFolder);
	}


	/**
	 * Creates the File object and the actual file in the file system for a new save in the given parent folder.
	 * 
	 * @param parent the parent of the new save
	 * @return the file object
	 */
	private static File createFileForNewSave(Folder parent)
	{
		if (GamesManager.getSelectedGame().getSaveFileLocation() == null)
		{
			JOptionPane.showMessageDialog(OrganizerManager.getMainWindow(),
					"To import a savefile you need to set the savefile location in the profile configuration settings!", "Error occurred",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		String parentPath = parent != null ? parent.getFile().getPath() : GamesManager.getSelectedProfile().getRoot().getFile().getPath();
		String name = GamesManager.getSelectedGame().getSaveName();
		File newFile = new File(parentPath + File.separator + name);
		for (int i = 0; newFile.exists(); i++)
			newFile = new File(parentPath + File.separator + name + "_" + i);
		try
		{
			Files.copy(GamesManager.getSelectedGame().getSaveFileLocation().toPath(), newFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(OrganizerManager.getMainWindow(), "Error when trying to import the savefile!", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
		}
		return newFile;
	}


	/**
	 * Copy entry into destination Folder object.
	 * 
	 * @param entry            the entry to copy
	 * @param dest             the Folder object to paste the file into and attach it to
	 * @param fireCreatedEvent whether to fire a entryCreated event
	 * @throws IOException
	 */
	public static void copyEntry(SaveListEntry entry, Folder dest, boolean fireCreatedEvent) throws IOException
	{
		File src = entry.getFile();

		String parentPath = dest.getFile().getPath();
		String name = src.getName();

		File newFile = new File(parentPath + File.separator + name);
		for (int i = 0; newFile.exists(); i++)
			newFile = new File(parentPath + File.separator + name + "_" + i);

		SaveListEntry newEntry;

		if (src.isDirectory())
		{
			OrganizerManager.copyDirectory(src, newFile);
			newEntry = new Folder(dest, newFile);
		}
		else
		{
			Files.copy(src.toPath(), newFile.toPath());
			newEntry = new Save(dest, newFile);
		}

		dest.addChild(newEntry);

		if (fireCreatedEvent)
			fireEntryCreatedEvent(newEntry);

	}


	/**
	 * Loads the given save and overwrites the current gamefile.
	 * 
	 * @param save the save to load
	 */
	public static void loadSave(Save save)
	{
		if (save == null)
			return;
		fireSaveLoadStartedEvent(save);
		Game game = GamesManager.getSelectedGame();
		File gameFile = game.getSaveFileLocation();
		File saveFile = save.getFile();
		boolean canWriteSaveFile = saveFile.canWrite();
		try
		{
			gameFile.setWritable(true);
			saveFile.setWritable(true);
			Files.copy(saveFile.toPath(), gameFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			gameFile.setWritable(canWriteSaveFile);
			saveFile.setWritable(canWriteSaveFile);
			AbstractMessage.display(AbstractMessage.SUCCESSFUL_LOAD);
		}
		catch (Exception e)
		{
//			JOptionPane.showMessageDialog(mainWindow, "Error when trying to load the savefile!", "Error occurred",
//					JOptionPane.ERROR_MESSAGE);
			AbstractMessage.display(AbstractMessage.FAILED_LOAD);
		}
		fireSaveLoadFinishedEvent(save);
	}


	/**
	 * Switches the gamefile of the currently selected game between read-only and writeable.
	 */
	public static void switchCurrentGameFileWritableState()
	{
		if (!GamesManager.getSelectedGame().supportsReadOnly())
			return;
		File gameFile = GamesManager.getSelectedGame().getSaveFileLocation();
		gameFile.setWritable(!gameFile.canWrite());
		fireGameFileWritableStateChangedEvent(gameFile.canWrite());
	}


	/**
	 * Sets the currently selected entry in the SaveList.
	 * 
	 * @param entry the selected entry
	 */
	public static void setSelectedEntry(SaveListEntry entry)
	{
		selectedEntry = entry;
		fireEntrySelectedEvent(entry);
	}


	/**
	 * Returns the currently selected entry in the SaveList.
	 * 
	 * @return the entry selected in the SaveList
	 */
	public static SaveListEntry getSelectedEntry()
	{
		return selectedEntry;
	}


	public static SortingCategory getSelectedSortingCategory()
	{
		String caption = SettingsManager.getStoredSelectedSortingCategoryName();
		for (SortingCategory category : SortingCategory.values())
		{
			if (category.getCaption().equals(caption))
				return category;
		}
		return SortingCategory.ALPHABET;
	}


	public static void setSelectedSortingCategory(SortingCategory category)
	{
		SettingsManager.setStoredSelectedSortingCategoryName(category.getCaption());
		fireSortingChangedEvent(category);
	}


	/**
	 * Adds a save listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSaveListener(SaveListener listener)
	{
		if (listener != null)
			saveListeners.add(listener);
	}


	/**
	 * Adds a search listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSearchListener(SearchListener listener)
	{
		if (listener != null)
			searchListeners.add(listener);
	}


	/**
	 * Adds a sorting listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addSortingListener(SortingListener listener)
	{
		if (listener != null)
			sortingListeners.add(listener);
	}


	/**
	 * Adds a navigation listener to send events to.
	 * 
	 * @param listener the listener to add
	 */
	public static void addNavigationListener(NavigationListener listener)
	{
		if (listener != null)
			navigationListeners.add(listener);
	}


	/**
	 * Fires an entryCreated event.
	 * 
	 * @param entry the entry that was created
	 */
	public static void fireEntryCreatedEvent(SaveListEntry entry)
	{
		for (SaveListener saveListener : saveListeners)
		{
			saveListener.entryCreated(entry);
		}
	}


	/**
	 * Fires an entryRenamed event.
	 * 
	 * @param entry the entry that was renamed
	 */
	public static void fireEntryRenamedEvent(SaveListEntry entry)
	{
		for (SaveListener saveListener : saveListeners)
		{
			saveListener.entryRenamed(entry);
		}
	}


	/**
	 * Fires an entrySelected event.
	 * 
	 * @param entry the entry that was selected
	 */
	public static void fireEntrySelectedEvent(SaveListEntry entry)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.entrySelected(entry);
		}
	}


	/**
	 * Fires a saveLoadStarted event.
	 * 
	 * @param save the save that is loaded
	 */
	public static void fireSaveLoadStartedEvent(Save save)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.saveLoadStarted(save);
		}
	}


	/**
	 * Fires a saveLoadFinished event.
	 * 
	 * @param save the save that was loaded
	 */
	public static void fireSaveLoadFinishedEvent(Save save)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.saveLoadFinished(save);
		}
	}


	/**
	 * Fires a gameFileWritableStateChanged event.
	 * 
	 * @param save the save that was loaded
	 */
	public static void fireGameFileWritableStateChangedEvent(boolean writeable)
	{
		for (SaveListener listener : saveListeners)
		{
			listener.gameFileWritableStateChanged(writeable);
		}
	}


	/**
	 * Fires a searchRequested event.
	 * 
	 * @param input the search input
	 */
	public static void fireSearchRequestedEvent(String input)
	{
		for (SearchListener listener : searchListeners)
		{
			listener.searchRequested(input);
		}
	}


	/**
	 * Fires a sortingChanged event.
	 * 
	 * @param category the category that was changed to
	 */
	public static void fireSortingChangedEvent(SortingCategory category)
	{
		for (SortingListener listener : sortingListeners)
		{
			listener.sortingChanged(category);
		}
	}


	/**
	 * Fires a navigatedToPrevious event.
	 */
	public static void fireNavigatedToPreviousEvent()
	{
		for (NavigationListener listener : navigationListeners)
		{
			listener.navigatedToPrevious();
		}
	}


	/**
	 * Fires a navigatedToNext event.
	 */
	public static void fireNavigatedToNextEvent()
	{
		for (NavigationListener listener : navigationListeners)
		{
			listener.navigatedToNext();
		}
	}

}
