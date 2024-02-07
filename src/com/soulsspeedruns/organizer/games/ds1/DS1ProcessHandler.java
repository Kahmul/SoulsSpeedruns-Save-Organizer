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

	private static long equipIndicesStartAddress = -1;


	private DS1ProcessHandler()
	{
	}


	@Override
	protected int getMinimumProcessLifeTime()
	{
		return MIN_PROCESS_LIFETIME;
	}


	@Override
	protected void processHandleOpened()
	{
		int[] pattern = getPatternFromAOB("24 13 00 00 FF FF FF FF FF FF FF FF 00 00 C0 3F 33 33 13 40");
		equipIndicesStartAddress = scanForAOB(pattern);
	}


	@Override
	protected void processHandleClosed()
	{
		equipIndicesStartAddress = -1;
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

		return writeInt(equipIndicesStartAddress + slot, null, combined);
	}


	/**
	 * Gets the last selected index of the given equip slot.
	 * 
	 * @param slot the equip slot. Use the static slot variables in DS1ProcessHandler
	 * @return the last selected index for the given equip slot, -1 if none is set or no process is available
	 */
	public short getEquipSlotIndex(int slot)
	{
		int combined = readInt(equipIndicesStartAddress + slot, null);
		if (combined == -1)
			return -1;

		short scrollBarValue = (short) (combined >> 16);
		short relativeIndex = (short) combined;

		return (short) (scrollBarValue + relativeIndex);
	}

}
