package com.soulsspeedruns.organizer.settings;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.soulsspeedruns.organizer.hotkeys.GlobalHotkey;


/**
 * HotkeyTextField.
 * <p>
 * Textfield for changing the global hotkey for an action.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Jun 2016
 */
public class HotkeyTextField extends JTextField implements FocusListener, KeyListener
{

	private static final String WAITING_FOR_INPUT_TEXT = "Set Hotkey...";

	private String currentKey;
	private GlobalHotkey hotkey;


	/**
	 * Creates a textfield for a hotkey.
	 * 
	 * @param hotkey the hotkey to create this textfield for
	 */
	protected HotkeyTextField(GlobalHotkey hotkey)
	{
		super(hotkey.getKeyCode());
		this.hotkey = hotkey;
		currentKey = getText();

		addFocusListener(this);
		addKeyListener(this);
		setHorizontalAlignment(JTextField.CENTER);
		setEditable(false);
	}


	/**
	 * @return the hotkey
	 */
	public GlobalHotkey getHotkey()
	{
		return hotkey;
	}


	/**
	 * Saves the changed hotkey shortcut to the associated hotkey.
	 */
	protected void saveChangesToHotkey()
	{
		hotkey.setKeyCode(currentKey);
	}


	@Override
	public void focusGained(FocusEvent e)
	{
		setText(WAITING_FOR_INPUT_TEXT);
	}


	@Override
	public void focusLost(FocusEvent e)
	{
		setText(currentKey);
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ESCAPE)
		{
			currentKey = "None";
			setText("None");
			getParent().requestFocusInWindow();
			return;

		}
		if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT)
			return;
		updateKey(e);
	}


	@Override
	public void keyReleased(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT)
		{
			// if only modifier keys have been pressed so far and one is released, use them for the keycode
			if (WAITING_FOR_INPUT_TEXT.equals(getText()))
				updateKey(e);
		}
		getParent().requestFocusInWindow();
	}


	/**
	 * Updates the hotkey of this hotkey field with the given KeyEvent.
	 * 
	 * @param e the KeyEvent
	 */
	private void updateKey(KeyEvent e)
	{
		String modifiers = KeyEvent.getKeyModifiersText(e.getModifiers());
		modifiers = modifiers.replaceAll("\\+", " \\+ ");
		modifiers = modifiers.length() > 0 ? modifiers + " + " : modifiers;

		String keyText = KeyEvent.getKeyText(e.getKeyCode());
		keyText = keyText.replaceAll("NumPad-", "NumPad ");

		setText(modifiers + keyText);
		currentKey = getText();
	}

}
