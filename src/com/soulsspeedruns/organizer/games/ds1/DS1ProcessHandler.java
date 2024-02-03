/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


import java.util.ArrayList;
import java.util.List;

import com.soulsspeedruns.organizer.games.GameProcessHandler;


/**
 * DS1ProcessHandler.
 * <p>
 * DS1 specific implementation of GameProcessHandler. Offers methods to read and write DS1 specific memory addresses.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 Jan 2024
 */
public class DS1ProcessHandler extends GameProcessHandler
{

	public static final DS1ProcessHandler INSTANCE = new DS1ProcessHandler();

	private static final String WINDOW_TITLE = "DARK SOULS";

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

	private static DS1Version version;


	private DS1ProcessHandler()
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


	@Override
	protected void init()
	{
		int versionFlag = readInt(VERSION_ADDRESS, null);

		for (DS1Version version : DS1Version.VERSIONS)
		{
			if (version.getVersionFlag() == versionFlag)
				DS1ProcessHandler.version = version;
		}
	}


	@Override
	protected void close()
	{
		version = null;
	}


	@Override
	protected String getWindowTitle()
	{
		return WINDOW_TITLE;
	}


	/**
	 * Sets the last selected index of the given equip slot to the given value.
	 * 
	 * @param slot  the equip slot. Use the static slot variables in DS1ProcessHandler
	 * @param index the index to set the slot to
	 * @return whether the memory write action was successful
	 */
	public boolean setEquipSlotIndex(int slot, int index)
	{
		return writeInt(version.getEquipSlotIndicesBaseAddress() + slot, null, index);
	}


	/**
	 * Gets the last selected index of the given equip slot.
	 * 
	 * @param slot the equip slot. Use the static slot variables in DS1ProcessHandler
	 * @return the last selected index for the given equip slot, -1 if none is set or no process is available
	 */
	public int getEquipSlotIndex(int slot)
	{
		return readInt(version.getEquipSlotIndicesBaseAddress() + slot, null);
	}


	public String getNameForEquipSlot(int slot)
	{
		return equipSlots.get(slot / EQUIP_SLOT_SIZE);
	}


	public int getEquipSlotForName(String name)
	{
		return equipSlots.indexOf(name) * EQUIP_SLOT_SIZE;
	}


	public List<String> getEquipSlots()
	{
		return equipSlots;
	}

}
