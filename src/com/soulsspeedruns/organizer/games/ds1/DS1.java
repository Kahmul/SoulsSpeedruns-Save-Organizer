/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * DS1.
 * <p>
 * Represents the various DS1 game versions and their respective base memory addresses.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 1 Feb 2024
 */
public abstract class DS1
{

	public static final DS1 RELEASE = new DS1Release();
	public static final DS1 DEBUG = new DS1Debug();
	public static final DS1 STEAMBETA = new DS1SteamBeta();

	public static final List<DS1> VERSIONS = new ArrayList<>(Arrays.asList(RELEASE, DEBUG, STEAMBETA));

	private static final long VERSION_ADDRESS = 0x400080;

	public static final int EQUIP_SLOT_SIZE = 0x4;
	
	public static final int LH1 = 0x0;
	public static final int RH1 = 0x4;
	public static final int LH2 = 0x8;
	public static final int RH2 = 0xC;
	public static final int ARROW1 = 0x10;
	public static final int BOLT1 = 0x14;
	public static final int ARROW2 = 0x18;
	public static final int BOLT2 = 0x1C;
	public static final int HEAD = 0x20;
	public static final int CHEST = 0x24;
	public static final int ARMS = 0x28;
	public static final int LEGS = 0x2C;
	public static final int HAIR = 0x30;
	public static final int RING1 = 0x34;
	public static final int RING2 = 0x38;
	public static final int HOTBAR1 = 0x3C;
	public static final int HOTBAR2 = 0x40;
	public static final int HOTBAR3 = 0x44;
	public static final int HOTBAR4 = 0x48;
	public static final int HOTBAR5 = 0x4C;

	private static List<String> equipSlots;

	static
	{
		equipSlots = new ArrayList<>(20);

		equipSlots.add("LH1");
		equipSlots.add("RH1");
		equipSlots.add("LH2");
		equipSlots.add("RH2");
		equipSlots.add("ARROW1");
		equipSlots.add("BOLT1");
		equipSlots.add("ARROW2");
		equipSlots.add("BOLT2");
		equipSlots.add("HEAD");
		equipSlots.add("CHEST");
		equipSlots.add("ARMS");
		equipSlots.add("LEGS");
		equipSlots.add("HAIR");
		equipSlots.add("RING1");
		equipSlots.add("RING2");
		equipSlots.add("HOTBAR1");
		equipSlots.add("HOTBAR2");
		equipSlots.add("HOTBAR3");
		equipSlots.add("HOTBAR4");
		equipSlots.add("HOTBAR5");
	}


	public static String getNameForEquipSlot(int slot)
	{
		return equipSlots.get(slot / EQUIP_SLOT_SIZE);
	}


	public static int getEquipSlotForName(String name)
	{
		return equipSlots.indexOf(name) * EQUIP_SLOT_SIZE;
	}


	public static List<String> getEquipSlots()
	{
		return equipSlots;
	}


	public static long getVersionAddress()
	{
		return VERSION_ADDRESS;
	}


	public abstract long getVersionFlag();

	public abstract long getEquipSlotIndicesBaseAddress();


	private static class DS1Release extends DS1
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


	private static class DS1Debug extends DS1
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


	private static class DS1SteamBeta extends DS1
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
