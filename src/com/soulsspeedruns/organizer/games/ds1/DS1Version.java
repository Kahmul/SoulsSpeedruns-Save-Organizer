/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * DS1Version.
 * <p>
 * Represents the various DS1 game versions and their respective base memory addresses.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 1 Feb 2024
 */
public abstract class DS1Version
{

	public static final DS1Version RELEASE = new DS1Release();
	public static final DS1Version DEBUG = new DS1Debug();
	public static final DS1Version STEAMBETA = new DS1SteamBeta();

	public static final List<DS1Version> VERSIONS = new ArrayList<>(Arrays.asList(RELEASE, DEBUG, STEAMBETA));


	public abstract long getVersionFlag();

	public abstract long getEquipSlotIndicesBaseAddress();


	private static class DS1Release extends DS1Version
	{

		@Override
		public long getVersionFlag()
		{
			return 0xFC293654;
		}


		@Override
		public long getEquipSlotIndicesBaseAddress()
		{
			return 0x12DEB60;
		}
	}


	private static class DS1Debug extends DS1Version
	{

		@Override
		public long getVersionFlag()
		{
			return 0xCE9634B4;
		}


		@Override
		public long getEquipSlotIndicesBaseAddress()
		{
			return 0x12E2B60;
		}
	}


	private static class DS1SteamBeta extends DS1Version
	{

		@Override
		public long getVersionFlag()
		{
			return 0xE91B11E2;
		}


		@Override
		public long getEquipSlotIndicesBaseAddress()
		{
			return 0x12DBB60;
		}
	}

}
