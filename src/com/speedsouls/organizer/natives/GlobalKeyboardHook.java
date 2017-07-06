package com.speedsouls.organizer.natives;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.speedsouls.organizer.content.GlobalHotkey;
import com.speedsouls.organizer.data.OrganizerManager;


/**
 * GlobalKeyboardHook.
 * <p>
 * Registers global hotkeys.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 20 May 2016
 */
public class GlobalKeyboardHook implements NativeKeyListener
{

	private boolean hotkeysEnabled = false;

	// Current modification keys being held
	List<String> mods = new ArrayList<>();

	String tempKey = "";


	/**
	 * Registers a global hotkey hook.
	 * 
	 * @throws NativeHookException
	 */
	public GlobalKeyboardHook() throws NativeHookException
	{
		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(this);
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}


	/**
	 * Registers the global hotkey hook.
	 */
	public void registerHook()
	{
		try
		{
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Unregisters the global hotkey hook.
	 */
	public void unregisterHook()
	{
		try
		{
			GlobalScreen.unregisterNativeHook();
		}
		catch (NativeHookException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Toggles global hotkeys.
	 * 
	 * @param flag True to enable hotkeys, false to disable them
	 */
	public void setHotkeysEnabled(boolean flag)
	{
		hotkeysEnabled = flag;
	}


	/**
	 * Returns whether global hotkeys are enabled.
	 * 
	 * @return whether global hotkeys are enabled
	 */
	public boolean areHotkeysEnabled()
	{
		return hotkeysEnabled;
	}


	@Override
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
		if (keyText.contains("Shift") || keyText.contains("Alt") || keyText.contains("Control"))
		{
			if ("".equals(tempKey))
			{
				if (mods.size() == 0)
					return;
				for (int i = 0; i < mods.size(); i++)
				{
					tempKey += mods.get(i);
					if (i < mods.size() - 1)
						tempKey += " + ";
				}
				mods.clear();
				for (GlobalHotkey hotkey : GlobalHotkey.values())
				{
					if (!hotkeysEnabled && hotkey != GlobalHotkey.GLOBAL_HOTKEY_TOGGLE)
						continue;
					if (tempKey.equals(hotkey.getKeyCode()))
					{
						hotkey.action();
						break;
					}
				}
			}
		}
		if (keyText.contains("Shift"))
			mods.remove("Shift");
		if (keyText.contains("Control"))
			mods.remove("Control");
		if (keyText.contains("Alt"))
			mods.remove("Alt");
		if (mods.size() == 0)
			tempKey = "";

	}


	@Override
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		if (NativeKeyEvent.getKeyText(e.getKeyCode()).equals("F1") && mods.size() == 0 && hotkeysEnabled)
		{
			OrganizerManager.openWebPage();
			return;
		}
		if (NativeKeyEvent.getKeyText(e.getKeyCode()).contains("Shift"))
		{
			if (!mods.contains("Shift"))
				mods.add("Shift");
			Collections.sort(mods);
			return;
		}
		if (NativeKeyEvent.getKeyText(e.getKeyCode()).contains("Control"))
		{
			if (!mods.contains("Control"))
				mods.add("Control");
			Collections.sort(mods);
			return;
		}
		if (NativeKeyEvent.getKeyText(e.getKeyCode()).contains("Alt"))
		{
			if (!mods.contains("Alt"))
				mods.add("Alt");
			Collections.sort(mods);
			return;
		}
		tempKey = "";
		for (int i = 0; i < mods.size(); i++)
			tempKey += mods.get(i) + " + ";
		tempKey += NativeKeyEvent.getKeyText(e.getKeyCode());

		for (GlobalHotkey hotkey : GlobalHotkey.values())
		{
			if (!hotkeysEnabled && hotkey != GlobalHotkey.GLOBAL_HOTKEY_TOGGLE)
				continue;
			if (tempKey.equals(hotkey.getKeyCode()))
			{
				hotkey.action();
				break;
			}
		}
	}


	@Override
	public void nativeKeyTyped(NativeKeyEvent e)
	{
	}

}
