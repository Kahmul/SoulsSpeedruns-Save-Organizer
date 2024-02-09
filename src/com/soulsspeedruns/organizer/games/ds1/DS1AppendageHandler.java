/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


import java.util.List;
import java.util.Map;

import com.soulsspeedruns.organizer.games.GameAppendageEditorWindow;
import com.soulsspeedruns.organizer.games.GameAppendageHandler;
import com.soulsspeedruns.organizer.savelist.Save;


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


	@Override
	protected String retrieveAppendedDataFromProcess()
	{
		DS1ProcessHandler handler = DS1ProcessHandler.INSTANCE;

		String data = "";
		List<String> equipSlots = DS1.getEquipSlots();

		for (int i = 0; i < equipSlots.size(); i++)
		{
			int slot = DS1.EQUIP_SLOT_SIZE * i;
			short index = handler.getEquipSlotIndex(slot);
			if (index == -1)
				continue;

			data += createNewKeyValuePair(equipSlots.get(i), String.valueOf(index));
		}

		return data;
	}


	@Override
	protected boolean writeAppendedDataToProcess(String data)
	{
		DS1ProcessHandler handler = DS1ProcessHandler.INSTANCE;

		List<String> equipSlots = DS1.getEquipSlots();
		Map<String, String> valuesMap = getValuesMapFromAppendedData(data);

		for (int i = 0; i < equipSlots.size(); i++)
		{
			String index = valuesMap.get(equipSlots.get(i));
			if (index == null)
				continue;

			boolean successful = handler.setEquipSlotIndex(DS1.EQUIP_SLOT_SIZE * i, Short.valueOf(index));
			if (!successful)
				return false;
		}

		return true;
	}


	@Override
	public boolean supportsManualEditing()
	{
		return true;
	}


	@Override
	public GameAppendageEditorWindow getEditorWindow(Save save)
	{
		return new DS1AppendageEditorWindow(this, save);
	}


	@Override
	public String getEditorWindowName()
	{
		return "Edit Equipment Slot Indices";
	}

}
