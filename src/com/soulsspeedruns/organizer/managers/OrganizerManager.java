package com.soulsspeedruns.organizer.managers;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.soulsspeedruns.organizer.games.GameProcessHandler;
import com.soulsspeedruns.organizer.main.OrganizerWindow;
import com.soulsspeedruns.organizer.theme.DefaultTheme;
import com.soulsspeedruns.organizer.theme.GlobalThemeAdjustmentTask;
import com.soulsspeedruns.organizer.theme.GlobalThemeInitTask;
import com.soulsspeedruns.organizer.theme.SoulsSpeedrunsTheme;


/**
 * OrganizerManager.
 * <p>
 * Main class. Calls other managers to initialize for application use. Handles L&F and utility methods.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class OrganizerManager
{

	/**
	 * Constants defining various URLs.
	 */
	public static final String WEB_PAGE_URL = "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer";
	public static final String GITHUB_REPO_URL = "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer";
	public static final String TWITTER_URL = "https://twitter.com/Kahmul78";

	public static final String ILLEGAL_CHARACTERS = "~, @, *, {, }, <, >, [, ], |, \u201C, \u201D, \\, /, ^";
	private static final String ILLEGAL_CHARACTERS_REGEX = "[~#@*{}<>\\[\\]|\"\\^\\\\\\/]";

	private static OrganizerWindow mainWindow;

//	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);


	public static void main(String[] args)
	{
		try
		{
			VersionManager.initialize();
			IconsAndFontsManager.initialize();
			SettingsManager.initialize();
			GamesManager.initialize();
			SavesManager.initialize();
			initialize();
			
			GameProcessHandler.startHookThread();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error when trying to initialize the organizer. Could not start the Save Organizer.",
					"Error occurred", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		SwingUtilities.invokeLater(() -> {
			new OrganizerWindow();
		});
	}


	/**
	 * Initializes the L&F and other global values.
	 * 
	 * @throws IOException
	 */
	private static void initialize() throws IOException
	{
		initLookAndFeel();
		initSharedValues();

//		setAppUserModelID();
	}


	/**
	 * Sets up the LafManager and the custom themes.
	 */
	private static void initLookAndFeel()
	{
		LafManager.unregisterTheme(new IntelliJTheme());
		LafManager.registerTheme(new SoulsSpeedrunsTheme());
		LafManager.registerTheme(new DefaultTheme());

		LafManager.registerDefaultsAdjustmentTask(new GlobalThemeAdjustmentTask());
		LafManager.registerInitTask(new GlobalThemeInitTask());

		LafManager.install(SettingsManager.getStoredTheme());
	}


	/**
	 * Initializes values shared across the application.
	 */
	private static void initSharedValues()
	{
		ToolTipManager.sharedInstance().setDismissDelay(60000);
	}

//	/**
//	 * Sets the AppUserModelID. Needed to be able to properly pin the .exe to the taskbar.
//	 */
//	private static void setAppUserModelID()
//	{
//		Native.register("shell32");
//		
//		WString appID = new WString("com.soulsspeedruns.saveorganizer");
//		SetCurrentProcessExplicitAppUserModelID(appID);
//	}


	/**
	 * Sets the main window for the manager.
	 * 
	 * @param window the main window
	 */
	public static void setMainWindow(OrganizerWindow window)
	{
		mainWindow = window;
	}


	/**
	 * @return the main window
	 */
	public static OrganizerWindow getMainWindow()
	{
		return mainWindow;
	}


	/**
	 * Opens the SoulsSpeedruns webpage for the Save Organizer in the default browser.
	 */
	public static void openWebPage()
	{
		try
		{
			Desktop.getDesktop().browse(new URI(WEB_PAGE_URL));
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Error occurred", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Opens the GitHub latest release page.
	 */
	public static void openLatestReleasePage()
	{
		try
		{
			Desktop.getDesktop().browse(new URI(VersionManager.getLatestReleaseURL()));
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Error occurred", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Checks whether the given file contains the given string in its name, and if not checks its subcontents for such a file if the given file is a
	 * directory.
	 * 
	 * @param directory the file/directory to check
	 * @param name      the name to check for
	 * @return whether a file containing the name was found or not
	 */
	public static boolean containsFileWithName(File directory, String name)
	{
		if (directory.exists())
		{
			if (directory.getName().toLowerCase().contains(name.toLowerCase()))
				return true;
			File[] files = directory.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].getName().toLowerCase().contains(name.toLowerCase()))
						return true;
					else if (files[i].isDirectory())
						if (containsFileWithName(files[i], name))
							return true;
				}
			}
		}
		return false;
	}


	/**
	 * Copies the source directory and its contents into the destination directory.
	 * 
	 * @param src  the source directory
	 * @param dest the destination directory
	 * @throws IOException
	 */
	public static void copyDirectory(File src, File dest) throws IOException
	{
		if (src.getPath().equals(dest.getPath()))
			return;
		if (isDirectoryAParentOfChild(src, dest))
		{
			JOptionPane.showMessageDialog(mainWindow, "The requested action would result in file recursion!", "Error occurred",
					JOptionPane.ERROR_MESSAGE);
			throw new IOException();
		}
		if (src.isDirectory())
		{
			if (!dest.exists())
				dest.mkdir();
			File[] files = src.listFiles();
			for (File file : files)
			{
				File srcFile = new File(src.getPath(), file.getName());
				File destFile = new File(dest.getPath(), file.getName());
				copyDirectory(srcFile, destFile);
			}
			return;
		}
		Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}


	/**
	 * Returns whether the given possibleChild is a file below the possibleParent in the file tree.
	 * 
	 * @param possibleParent the possible parent file to check
	 * @param possibleChild the possible child file to check
	 * @return whether the child can be found somewhere below the parent in the file tree.
	 */
	public static boolean isDirectoryAParentOfChild(File possibleParent, File possibleChild)
	{
		File parent = possibleChild.getParentFile();
		while (parent != null)
		{
			if (parent.equals(possibleParent))
				return true;
			parent = parent.getParentFile();
		}
		return false;
	}


	/**
	 * Deletes the given directory and all of its sub folders, or simply deletes the given file if its not a directory.
	 * 
	 * @param directory the directory to delete
	 * @return whether the deletion was successful or not
	 */
	public static boolean deleteDirectory(File directory)
	{
		if (directory.exists())
		{
			File[] files = directory.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						deleteDirectory(files[i]);
					}
					else
					{
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}


	/**
	 * Checks the given string for illegal characters.
	 * 
	 * @param toExamine the string to examine
	 * @return true if the string contains illegal characters. False otherwise.
	 */
	public static boolean containsIllegals(String toExamine)
	{
		String[] arr = toExamine.split(ILLEGAL_CHARACTERS_REGEX, 2);
		return arr.length > 1;
	}

}
