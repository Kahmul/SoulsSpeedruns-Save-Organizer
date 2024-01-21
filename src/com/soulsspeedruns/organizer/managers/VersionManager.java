/**
 * 
 */
package com.soulsspeedruns.organizer.managers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;


/**
 * VersionManager.
 * <p>
 * Handles methods around the application version and the system environment.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 20 Jan 2024
 */
public class VersionManager
{

	private static final String VERSION = "1.5.1";

	private static final String LATEST_RELEASE_JSON_URL = "https://api.github.com/repos/Kahmul/SoulsSpeedruns-Save-Organizer/releases/latest";
	private static final String LATEST_RELEASE_URL = "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/releases/latest";

	private static String latestReleaseVersion;

	private static String operatingSystem;


	protected static void initialize()
	{
		determineOS();
	}


	/**
	 * Determines the general OS name the application is running on.
	 */
	private static void determineOS()
	{
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
		{
			operatingSystem = "Windows";
		}
		else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix"))
		{
			operatingSystem = "Linux";
		}
		else if (osName.contains("mac"))
		{
			operatingSystem = "Mac";
		}
	}


	/**
	 * Returns the save organizer version.
	 * 
	 * @return the current version
	 */
	public static String getVersion()
	{
		return VERSION;
	}


	/**
	 * Returns the URL to the latest release on GitHub.
	 * 
	 * @return latest github release URl
	 */
	public static String getLatestReleaseURL()
	{
		return LATEST_RELEASE_URL;
	}


	/**
	 * Returns whether the OS this application is running on is Windows.
	 * 
	 * @return whether the OS is Windows
	 */
	public static boolean isRunningOnWindows()
	{
		return operatingSystem.contains("Windows");
	}


	/**
	 * Returns whether the OS this application is running on is Linux. It is assumed for the Souls games that they are running on a compatibility
	 * layer such as Proton.
	 * 
	 * @return whether the OS is Linux
	 */
	public static boolean isRunningOnLinux()
	{
		return operatingSystem.contains("Linux");
	}


	/**
	 * Gets the major Java version of the current runtime.
	 * 
	 * @return the currently used major Java version
	 */
	public static int getMajorJavaVersion()
	{
		String[] versionElements = System.getProperty("java.version").split("\\.");
		int firstElement = Integer.parseInt(versionElements[0]);
		int version = firstElement == 1 ? Integer.parseInt(versionElements[1]) : firstElement;

		return version;
	}


	/**
	 * Checks whether the local Save Organizer version is outdated compared to the latest GitHub release.
	 * 
	 * @return whether the local version is outdated
	 */
	public static boolean isVersionOutdated()
	{
		if (!SettingsManager.isCheckForUpdatesEnabled())
			return false;

		if (latestReleaseVersion == null)
			latestReleaseVersion = getLatestReleaseVersion();

		String[] vals1 = VERSION.split("\\.");
		String[] vals2 = latestReleaseVersion.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
		{
			i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length)
		{
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff) == -1;
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return Integer.signum(vals1.length - vals2.length) == -1;
	}


	/**
	 * Checks the latest release version on GitHub and returns it.
	 * 
	 * @return the latest release version on GitHub
	 */
	public static String getLatestReleaseVersion()
	{
		JSONObject latestReleaseJSON = getLatestReleaseJSON();
		if (latestReleaseJSON != null)
		{
			String version = latestReleaseJSON.getString("tag_name");
			String prefix = version.split("[0-9]")[0];
			version = version.substring(prefix.length());

			return version;
		}
		return "0.0";
	}


	/**
	 * Retrieves the description of the latest release from GitHub.
	 * 
	 * @return the latest release description
	 */
	public static String getLatestReleaseDescription()
	{
		JSONObject latestReleaseJSON = getLatestReleaseJSON();
		if (latestReleaseJSON != null)
			return latestReleaseJSON.getString("body");
		return "";
	}


	/**
	 * Builds the download URL based on the latest release version.
	 * 
	 * @return the download URL for the latest release
	 */
	public static String getLatestReleaseDownloadURL()
	{
		String latestVersion = getLatestReleaseVersion();
		return "https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/releases/download/v." + latestVersion + "SoulsSpeedruns.-.Save.Organizer."
				+ latestVersion + ".zip";
	}


	/**
	 * Creates a JSONObject of the latest release on GitHub.
	 * 
	 * @return the JSONObject of the latest release
	 */
	private static JSONObject getLatestReleaseJSON()
	{
		try (InputStream is = URI.create(LATEST_RELEASE_JSON_URL).toURL().openStream())
		{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		}
		catch (Exception e)
		{
			return null;
		}
	}


	/**
	 * Reads all the input from a Reader and returns it in a single String.
	 * 
	 * @param rd the reader
	 * @return the input
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1)
		{
			sb.append((char) cp);
		}
		return sb.toString();
	}

}
