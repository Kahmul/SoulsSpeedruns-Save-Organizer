package com.speedsouls.organizer.natives;


import java.awt.event.KeyEvent;
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

	// if a modifier key is released, this is used to decide whether the modifier was tied to a previous key combo or not
	private boolean keyComboWasExecuted = false;


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
	public void nativeKeyTyped(NativeKeyEvent e)
	{
	}


	@Override
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		keyComboWasExecuted = false;
		int keyCode = e.getKeyCode();
		if (keyCode == NativeKeyEvent.VC_F1 && e.getModifiers() == 0 && hotkeysEnabled)
		{
			OrganizerManager.openWebPage();
			return;
		}
		if (keyCode == NativeKeyEvent.VC_SHIFT_L || keyCode == NativeKeyEvent.VC_SHIFT_R || keyCode == NativeKeyEvent.VC_CONTROL_L
				|| keyCode == NativeKeyEvent.VC_CONTROL_R || keyCode == NativeKeyEvent.VC_ALT_L || keyCode == NativeKeyEvent.VC_ALT_R)
			return;
		executeKeyEvent(e);
	}


	@Override
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if (keyCode == NativeKeyEvent.VC_SHIFT_L || keyCode == NativeKeyEvent.VC_SHIFT_R || keyCode == NativeKeyEvent.VC_CONTROL_L
				|| keyCode == NativeKeyEvent.VC_CONTROL_R || keyCode == NativeKeyEvent.VC_ALT_L || keyCode == NativeKeyEvent.VC_ALT_R)
		{
			// if only modifiers have been held so far and one is released, then execute the action
			if (!keyComboWasExecuted)
				executeKeyEvent(e);
		}
	}


	/**
	 * Executes the action of the hotkey with the same key code as the given KeyEvent.
	 * 
	 * @param e the KeyEvent
	 */
	private void executeKeyEvent(NativeKeyEvent e)
	{
		String keyText = getKeyText(e);
		System.out.println("KeyText: " + keyText);
		for (GlobalHotkey hotkey : GlobalHotkey.values())
		{
			if (!hotkeysEnabled && hotkey != GlobalHotkey.GLOBAL_HOTKEY_TOGGLE)
				continue;
			if (keyText.equals(hotkey.getKeyCode()))
			{
				hotkey.action();
				keyComboWasExecuted = true;
				break;
			}
		}
	}


	/**
	 * Gets the key combination text from the KeyEvent.
	 * 
	 * @param e the KeyEvent
	 * @return the combined text of all pressed keys
	 */
	private String getKeyText(NativeKeyEvent e)
	{
		String modifiers = NativeKeyEvent.getModifiersText(e.getModifiers());
		modifiers = modifiers.replaceAll("\\+", " \\+ ");
		modifiers = modifiers.length() > 0 ? modifiers + " + " : modifiers;

		String keyText = "";

		// NativeKeyEvent has different keytexts than Java for modifiers, so need to standardize them
		switch (e.getKeyCode())
		{
		case NativeKeyEvent.VC_SHIFT_L:
		case NativeKeyEvent.VC_SHIFT_R:
			keyText = KeyEvent.getKeyText(KeyEvent.VK_SHIFT);
			break;
		case NativeKeyEvent.VC_CONTROL_L:
		case NativeKeyEvent.VC_CONTROL_R:
			keyText = KeyEvent.getKeyText(KeyEvent.VK_CONTROL);
			break;
		case NativeKeyEvent.VC_ALT_L:
		case NativeKeyEvent.VC_ALT_R:
			keyText = KeyEvent.getKeyText(KeyEvent.VK_ALT);
			break;
		default:
			keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
			break;
		}

		return modifiers + keyText;
	}

}
