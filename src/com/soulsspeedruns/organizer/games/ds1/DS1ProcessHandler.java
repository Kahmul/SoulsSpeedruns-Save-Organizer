/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


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

	private static DS1 version;


	private DS1ProcessHandler()
	{
	}


	@Override
	protected void init()
	{
		int versionFlag = readInt(DS1.getVersionAddress(), null);

		for (DS1 version : DS1.VERSIONS)
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
	public boolean setEquipSlotIndex(int slot, short index)
	{
		short scrollbarValue = (short) Math.max(0, index - 4);
		int combined = (index - scrollbarValue) | (scrollbarValue << 16);

		return writeInt(version.getEquipSlotIndicesBaseAddress() + slot, null, combined);
	}


	/**
	 * Gets the last selected index of the given equip slot.
	 * 
	 * @param slot the equip slot. Use the static slot variables in DS1ProcessHandler
	 * @return the last selected index for the given equip slot, -1 if none is set or no process is available
	 */
	public short getEquipSlotIndex(int slot)
	{
		int combined = readInt(version.getEquipSlotIndicesBaseAddress() + slot, null);
		if (combined == -1)
			return -1;

		short scrollBarValue = (short) (combined >> 16);
		short relativeIndex = (short) combined;

		return (short) (scrollBarValue + relativeIndex);
	}

}
