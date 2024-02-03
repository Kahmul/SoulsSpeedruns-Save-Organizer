/**
 * 
 */
package com.soulsspeedruns.organizer.games;


import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.swing.JFrame;

import com.soulsspeedruns.organizer.managers.GamesManager;


/**
 * GameAppendageHandler.
 * <p>
 * Base class handling writing/deleting appended data to/from savefiles. Retrieving/writing values from/to the process is handled by subclasses.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 2 Feb 2024
 */
public abstract class GameAppendageHandler
{

	private static final String PREFIX = "SSOData--";
	private static final String SUFFIX = "--SSOData";
	private static final byte[] SUFFIX_BYTES = SUFFIX.getBytes();
	private static final int SUFFIX_LENGTH = SUFFIX_BYTES.length;
	private static final int BYTES_SEARCH_STEP = 150;


	protected GameAppendageHandler()
	{
	}


	/**
	 * Retrieves data from the game process, if it is running, and appends it to the given file based on the game specific handlers.
	 * 
	 * @param file the file to append the data to
	 * @return whether the data was successfully retrieved and appended
	 */
	public static boolean saveAppendedDataToFile(File file)
	{
		if(!GamesManager.isDataAppendageAndProcessHandlingSupported())
			return false;

		if (!GameProcessHandler.isHooked())
			return false;

		GameAppendageHandler appendageHandler = GamesManager.getSelectedGame().getAppendageHandler();
		String data = appendageHandler.retrieveAppendedDataFromProcess();
		if (data == null)
			return false;

		return saveAppendedDataToFile(file, data);
	}


	/**
	 * Appends the given string data to the end of the given file.
	 * 
	 * @param file the file to append to
	 * @param data the string to append
	 * @return whether the data was successfully appended
	 */
	public static boolean saveAppendedDataToFile(File file, String data)
	{
		data = PREFIX + data + SUFFIX;

		removeAppendedDataFromFile(file);
		byte[] appendageBytes = data.getBytes();
		try (FileOutputStream output = new FileOutputStream(file.getPath(), true))
		{
			output.write(appendageBytes);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Takes the appended save data from the file, if any, and applies the values to the game process based on the game specific handlers.
	 * 
	 * @param file the file to read the appended data from
	 * @return whether the data was successfully applied to the game
	 */
	public static boolean applyAppendedDataToGame(File file)
	{
		if(!GamesManager.isDataAppendageAndProcessHandlingSupported())
			return false;

		if (!GameProcessHandler.isHooked())
			return false;

		String appendedData = getAppendedDataFromFile(file);
		System.out.println(appendedData);
		if (appendedData == null)
			return false;

		appendedData = appendedData.replaceFirst(PREFIX, "");
		appendedData = appendedData.replaceFirst(SUFFIX, "");
		
		GameAppendageHandler appendageHandler = GamesManager.getSelectedGame().getAppendageHandler();
		return appendageHandler.writeAppendedDataToProcess(appendedData);
	}


	/**
	 * Gets the appended save data from given file, if any.
	 * 
	 * @param file the file to get the appended data from
	 * @return the appended data
	 */
	public static String getAppendedDataFromFile(File file)
	{
		if (!hasAppendedData(file))
			return null;

		long fileSize = file.length();
		try (RandomAccessFile accessFile = new RandomAccessFile(file, "r"))
		{
			int i = 1;
			long seekIndex = 0;
			String appendedData = "";
			do
			{
				seekIndex = fileSize - BYTES_SEARCH_STEP * i;
				if (seekIndex < 0)
					seekIndex = 0;
				accessFile.seek(seekIndex);

				StringBuilder input = new StringBuilder();
				int c = -1;
				while ((c = accessFile.read()) != -1)
					input.append((char) c);

				appendedData = input.toString();
				i++;
			}
			while (appendedData.length() > 0 && !appendedData.contains(PREFIX) && seekIndex > 0);

			if (!appendedData.contains(PREFIX))
				return null;
			appendedData = appendedData.substring(appendedData.indexOf(PREFIX) + PREFIX.length(), appendedData.indexOf(SUFFIX));

			return appendedData;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Returns whether the given file has appended data.
	 * 
	 * @param file the file to check
	 * @return true if the file has appended data
	 */
	public static boolean hasAppendedData(File file)
	{
		try (RandomAccessFile accessFile = new RandomAccessFile(file, "r"))
		{
			accessFile.seek(file.length() - SUFFIX_LENGTH);
			byte[] buffer = new byte[SUFFIX_LENGTH];
			accessFile.read(buffer);

			return Arrays.equals(buffer, SUFFIX_BYTES);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Removes all appended save data, if any, from the given file.
	 * 
	 * @param file the file to remove the appended data from
	 */
	public static boolean removeAppendedDataFromFile(File file)
	{
		String appendedData = getAppendedDataFromFile(file);
		if (appendedData == null)
			return false;

		appendedData = PREFIX + appendedData + SUFFIX;

		try (RandomAccessFile accessFile = new RandomAccessFile(file, "rw"))
		{
			accessFile.setLength(file.length() - appendedData.getBytes().length);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Formats the values read from memory into a single string to append to a file.
	 * 
	 * @return the appended data as string
	 */
	protected abstract String retrieveAppendedDataFromProcess();

	/**
	 * Reads the values from the given appended save data and writes them to the game process.
	 * 
	 * @param data the data to write to the process as string
	 */
	protected abstract boolean writeAppendedDataToProcess(String data);

	/**
	 * Gets an instance of the window that allows editing the appended data of a savefile.
	 * 
	 * @return the editor window as a JFrame
	 */
	public abstract JFrame getEditorWindow();

}
