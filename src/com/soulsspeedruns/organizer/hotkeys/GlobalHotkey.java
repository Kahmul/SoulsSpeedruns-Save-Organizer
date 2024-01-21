package com.soulsspeedruns.organizer.hotkeys;


import javax.swing.JOptionPane;

import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.SavesManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.savelist.Save;


/**
 * Global Hotkey Enum.
 * <p>
 * Enum representing the different global hotkeys and their actions.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Jun 2016
 */
public enum GlobalHotkey
{

	LOAD_SAVE("Load Savestate:", "None")
	{

		@Override
		public void action()
		{
			if (SavesManager.getSelectedEntry() instanceof Save)
				SavesManager.loadSave((Save) SavesManager.getSelectedEntry());
		}


		@Override
		public String getPrefsKey()
		{
			return SettingsManager.PREFS_KEY_GLOBAL_HOTKEY_LOAD;
		}
	},
	IMPORT_SAVE("Import Savestate:", "None")
	{

		@Override
		public void action()
		{
			if (GamesManager.isAProfileSelected())
			{
				SavesManager.importSavefile(null);
				return;
			}
			JOptionPane.showMessageDialog(null,
					"Create a profile before trying to import a savefile! You can do this in the profile configuration settings.",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}


		@Override
		public String getPrefsKey()
		{
			return SettingsManager.PREFS_KEY_GLOBAL_HOTKEY_IMPORT_SAVE;
		}
	},
	READ_ONLY_TOGGLE("Switch Gamefile To Read-Only:", "None")
	{

		@Override
		public void action()
		{
			SavesManager.switchCurrentGameFileWritableState();
		}


		@Override
		public String getPrefsKey()
		{
			return SettingsManager.PREFS_KEY_GLOBAL_HOTKEY_READ_ONLY;
		}
	},
	PREV_SAVE_TOGGLE("Highlight Previous Savestate:", "None")
	{

		@Override
		public void action()
		{
			SavesManager.fireNavigatedToPreviousEvent();
		}


		@Override
		public String getPrefsKey()
		{
			return SettingsManager.PREFS_KEY_GLOBAL_HOTKEY_PREV_SAVE;
		}
	},
	NEXT_SAVE_TOGGLE("Highlight Next Savestate:", "None")
	{

		@Override
		public void action()
		{
			SavesManager.fireNavigatedToNextEvent();
		}


		@Override
		public String getPrefsKey()
		{
			return SettingsManager.PREFS_KEY_GLOBAL_HOTKEY_NEXT_SAVE;
		}
	},
	GLOBAL_HOTKEY_TOGGLE("Toggle Global Hotkeys:", "None")
	{

		@Override
		public void action()
		{
			SettingsManager.setGlobalHotkeysEnabled(!SettingsManager.areGlobalHotkeysEnabled());
		}


		@Override
		public String getPrefsKey()
		{
			return SettingsManager.PREFS_KEY_GLOBAL_HOTKEY_TOGGLE;
		}
	};

	private String caption;
	private String keyCode;


	/**
	 * Creates a new GlobalHotkey constant.
	 * 
	 * @param caption the name of the hotkey which is displayed in the settings window
	 * @param keyCode the associated key code on which the hotkey action will be executed
	 */
	private GlobalHotkey(String caption, String keyCode)
	{
		setCaption(caption);
		this.keyCode = keyCode;
	}


	/**
	 * The action to perform on pressing the hotkey.
	 */
	public abstract void action();


	public abstract String getPrefsKey();


	/**
	 * @return the caption
	 */
	public String getCaption()
	{
		return caption;
	}


	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}


	/**
	 * @return the keyCode
	 */
	public String getKeyCode()
	{
		String storedCode = SettingsManager.getStoredHotkeyCode(this);
		return "".equals(storedCode) ? keyCode : storedCode;

	}


	/**
	 * @param keyCode the keyCode to set
	 */
	public void setKeyCode(String keyCode)
	{
		this.keyCode = keyCode;
		SettingsManager.setStoredHotkeyCode(this, keyCode);
	}
}
