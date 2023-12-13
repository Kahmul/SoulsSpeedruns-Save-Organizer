package com.soulsspeedruns.organizer.savelist;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;


/**
 * Sorting Category Enum.
 * <p>
 * Enum representing the different sorting categories for the save list.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 19 May 2016
 */
public enum SortingCategory
{

	ALPHABET("Alphabet")
	{

		@Override
		public int compare(SaveListEntry s1, SaveListEntry s2)
		{
			return s1.getFile().getName().compareToIgnoreCase(s2.getName());
		}
	},
	DATE("Created")
	{

		@Override
		public int compare(SaveListEntry s1, SaveListEntry s2)
		{
			try
			{
				Path path = Paths.get(s1.getFile().getPath());
				BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
				FileTime s1CreationTime = attributes.creationTime();

				path = Paths.get(s2.getFile().getPath());
				attributes = Files.readAttributes(path, BasicFileAttributes.class);
				FileTime s2CreationTime = attributes.creationTime();
				return s2CreationTime.compareTo(s1CreationTime);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return 0;
		}
	},
	READ_ONLY("Read Only")
	{

		@Override
		public int compare(SaveListEntry s1, SaveListEntry s2)
		{
			boolean s1ReadOnly = !s1.getFile().canWrite();
			boolean s2ReadOnly = !s2.getFile().canWrite();
			return s1ReadOnly ? (s2ReadOnly ? 0 : -1) : (s2ReadOnly ? 1 : 0);
		}
	};

	private String caption;


	/**
	 * Creates a new SortingCategory constant.
	 * 
	 * @param caption the caption of the category
	 */
	private SortingCategory(String caption)
	{
		this.caption = caption;
	}


	/**
	 * Returns the string representation of the category.
	 * 
	 * @return the caption
	 */
	public String getCaption()
	{
		return caption;
	}


	public abstract int compare(SaveListEntry s1, SaveListEntry s2);

}
