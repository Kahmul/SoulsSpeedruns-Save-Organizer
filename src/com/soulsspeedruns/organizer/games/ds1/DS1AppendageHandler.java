/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


import java.util.List;

import javax.swing.JFrame;

import com.soulsspeedruns.organizer.games.GameAppendageHandler;


/**
 * DS1AppendageHandler.
 * <p>
 * DS1 specific implementation of GameProcessHandler. Implements methods to retrieve/write appended save data from/to the DS1 process.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 2 Feb 2024
 */
public class DS1AppendageHandler extends GameAppendageHandler
{

	public static final DS1AppendageHandler INSTANCE = new DS1AppendageHandler();

	private static final String SEPARATOR = "|";


	@Override
	protected String retrieveAppendedDataFromProcess()
	{
		DS1ProcessHandler handler = DS1ProcessHandler.INSTANCE;

		String data = "";
		List<String> equipSlots = handler.getEquipSlots();

		for (int i = 0; i < equipSlots.size(); i++)
		{
			int slot = DS1ProcessHandler.EQUIP_SLOT_SIZE * i;
			int index = handler.getEquipSlotIndex(slot);

			data += equipSlots.get(i) + "=" + index + SEPARATOR;
		}

		return data;
	}


	@Override
	protected boolean writeAppendedDataToProcess(String data)
	{
		DS1ProcessHandler handler = DS1ProcessHandler.INSTANCE;

		List<String> equipSlots = handler.getEquipSlots();

		for (int i = 0; i < equipSlots.size(); i++)
		{
			int indexOfSlot = data.indexOf(equipSlots.get(i));
			String keyValuePair = data.substring(indexOfSlot, data.indexOf(SEPARATOR, indexOfSlot));
			String index = keyValuePair.substring(equipSlots.get(i).length() + 1);

			boolean successful = handler.setEquipSlotIndex(DS1ProcessHandler.EQUIP_SLOT_SIZE * i, Integer.valueOf(index));
			if(!successful)
				return false;
		}
		
		return true;
	}


	@Override
	public JFrame getEditorWindow()
	{
		return null;
	}

}
