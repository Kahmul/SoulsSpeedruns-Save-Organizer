package com.soulsspeedruns.organizer.savelist;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.swing.JLabel;

import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * SaveListEntry class.
 * <p>
 * Class representing the entries in the Savelist.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 7 Jul 2017
 */
public abstract class SaveListEntry implements Comparable<SaveListEntry>, Transferable
{

	private Folder parent;
	private LinkedList<SaveListEntry> children;
	private File file;
	
	private boolean markedForCut;

	public static final DataFlavor ENTRY_FLAVOR = new DataFlavor(SaveListEntry.class, SaveListEntry.class.getSimpleName());


	/**
	 * @param parent
	 * @param file
	 */
	public SaveListEntry(Folder parent, File file)
	{
		this.parent = parent;
		this.file = file;
		children = new LinkedList<>();
	}


	/**
	 * Returns the parent folder of this entry.
	 * 
	 * @return the parent folder
	 */
	public Folder getParent()
	{
		return parent;
	}


	/**
	 * Returns the file associated with this entry.
	 * 
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}


	/**
	 * Replaces the currently associated file with the given one, and updates the associated files for all children.
	 * 
	 * @param file the new file
	 */
	public void setFile(File file)
	{
		String oldPath = getFile().getPath();
		this.file = file;
		
		// Make sure the files of any children are updated for the new parent path
		List<SaveListEntry> children = getChildren();
		for (SaveListEntry child : children)
		{
			String childPath = child.getFile().getPath();
			childPath = childPath.replace(oldPath, getFile().getPath());
			child.setFile(new File(childPath));
		}

	}


	/**
	 * Returns the filename of this entry.
	 * 
	 * @return the filename
	 */
	public String getName()
	{
		return file.getName();
	}


	/**
	 * Returns the indent used for the rendering in the SaveList.
	 * 
	 * @return the indent
	 */
	protected int getIndent()
	{
		if (parent.equals(OrganizerManager.getSelectedProfile().getRoot()))
			return 0;
		return parent.getIndent() + 20;
	}


	/**
	 * @return the children
	 */
	public LinkedList<SaveListEntry> getChildren()
	{
		return children;
	}


	/**
	 * @param entry
	 */
	public void addChild(SaveListEntry entry)
	{
		children.add(entry);
	}


	/**
	 * @param entry
	 */
	public void removeChild(SaveListEntry entry)
	{
		children.remove(entry);
	}
	
	/**
	 * Removes all children from this entry.
	 */
	public void clearChildren()
	{
		children.clear();
	}


	/**
	 * Sorts all children of this entry.
	 */
	public void sort()
	{
		Collections.sort(children);
		for (SaveListEntry entry : children)
			entry.sort();
	}

	
	/**
	 * Moves the entry and all of its children, if any, under the new parent folder.
	 * 
	 * @param newParent
	 * @throws IOException 
	 */
	public void moveToNewParent(Folder newParent) throws IOException
	{
		String parentPath = newParent.getFile().getPath();
		File newFile = new File(parentPath + File.separator + getName());
		
		Files.move(Paths.get(getFile().getPath()), Paths.get(newFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
		setFile(newFile);
		
		parent.removeChild(this);
		newParent.addChild(this);
		parent = newParent;
	}


	/**
	 * Returns whether this instance or any of its children are a parent of the given entry.
	 * 
	 * @param entry the entry to check
	 * @return whether this entry or any of its children are a parent of the given entry
	 */
	public boolean isParentOf(SaveListEntry entry)
	{
		if (this.equals(entry.getParent()))
			return true;
		for (SaveListEntry child : children)
		{
			if (child.isParentOf(entry))
				return true;
		}
		return false;
	}


	/**
	 * Returns the immediate child (no sub-child) of this entry with the given name.
	 * 
	 * @param name the name of the child
	 * @return the child with the given name, or null if none is found
	 */
	public SaveListEntry getChildByName(String name)
	{
		for (SaveListEntry child : children)
		{
			if (child.getName().equals(name))
				return child;
		}
		return null;
	}


	/**
	 * Returns true if either this entry or one of its children have the given searchTerm in their name.
	 * 
	 * @param searchTerm the term to search for
	 * @return whether this entry or one of its children matches the given term
	 */
	public boolean matchesSearchTerm(String searchTerm)
	{
		if (getName().toLowerCase().contains(searchTerm.toLowerCase()))
			return true;
		for (SaveListEntry entry : children)
		{
			if (entry.matchesSearchTerm(searchTerm))
				return true;
		}
		return false;
	}


	/**
	 * Renames the given entry.
	 * 
	 * @param newName the new name
	 * @return whether the renaming was successful
	 */
	public abstract boolean rename(String newName);


	/**
	 * Returns whether this entry can currently be renamed. Relevant to check if the file associated with the entry is currently
	 * being accessed elsewhere.
	 * 
	 * @return whether the entry can be renamed
	 */
	public abstract boolean canBeRenamed();


	/**
	 * Returns whether the entry is marked for a cut operation.
	 * 
	 * @return whether the entry is marked for a cut operation
	 */
	public boolean isMarkedForCut()
	{
		return markedForCut;
	}


	/**
	 * Marks this entry as part of a cut/paste operation.
	 * 
	 * @param markedForCut whether the entry is marked for a cut operation
	 */
	public void setMarkedForCut(boolean markedForCut)
	{
		this.markedForCut = markedForCut;
		for (SaveListEntry child : children) {
			child.setMarkedForCut(markedForCut);
		}
	}


	/**
	 * Renders this label according to this entry.
	 * 
	 * @param label the label to render
	 */
	public abstract void render(JLabel label);


	/**
	 * Deletes this entry and the associated file, along with all of its sub-folders and saves, if any.
	 */
	public abstract void delete();


	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (isDataFlavorSupported(flavor))
			return this;
		throw new UnsupportedFlavorException(flavor);
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
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SaveListEntry))
			return false;
		SaveListEntry entry = (SaveListEntry) o;
		return Objects.equals(parent, entry.parent) && Objects.equals(file, entry.file);
	}

}
