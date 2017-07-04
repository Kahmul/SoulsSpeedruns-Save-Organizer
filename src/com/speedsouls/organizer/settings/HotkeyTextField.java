package com.speedsouls.organizer.settings;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextField;

import com.speedsouls.organizer.content.GlobalHotkey;


/**
 * HotkeyTextField.
 * <p>
 * Textfield for changing the global hotkey for an action.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Jun 2016
 */
public class HotkeyTextField extends JTextField
{

	private static final long serialVersionUID = 154973469277989728L;

	private String tempKey;
	private GlobalHotkey hotkey;


	/**
	 * Creates a textfield for a hotkey.
	 * 
	 * @param hotkeythe hotkey to create this textfield for
	 */
	protected HotkeyTextField(GlobalHotkey hotkey)
	{
		super(hotkey.getKeyCode());
		this.hotkey = hotkey;
		tempKey = getText();

		addListeners();
		setHorizontalAlignment(JTextField.CENTER);
		setEditable(false);
	}


	/**
	 * Adds the necessary listeners to the textfield.
	 */
	private void addListeners()
	{
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e)
			{
				setText(tempKey);
			}


			@Override
			public void focusGained(FocusEvent e)
			{
				setText("Set Hotkey...");
			}
		});

		List<String> mods = new ArrayList<>();

		addKeyListener(new KeyListener() {

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
					tempKey = "None";
					setText("None");
					getParent().requestFocusInWindow();
					return;

				}
				Collections.sort(mods);
				if (keyCode == KeyEvent.VK_SHIFT)
				{
					if (!mods.contains("Shift"))
						mods.add("Shift");
					return;
				}
				if (keyCode == KeyEvent.VK_CONTROL)
				{
					if (!mods.contains("Control"))
						mods.add("Control");
					return;
				}
				if (keyCode == KeyEvent.VK_ALT)
				{
					if (!mods.contains("Alt"))
						mods.add("Alt");
					return;
				}
				tempKey = "";
				for (int i = 0; i < mods.size(); i++)
					tempKey += mods.get(i) + " + ";

				tempKey += KeyEvent.getKeyText(keyCode).replaceAll("NumPad-", "NumPad ");
				setText(tempKey);
			}


			@Override
			public void keyReleased(KeyEvent e)
			{
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT)
				{
					if (getText().equals("Set Hotkey..."))
					{
						tempKey = "";
						for (int i = 0; i < mods.size(); i++)
						{
							tempKey += mods.get(i);
							if (i < mods.size() - 1)
								tempKey += " + ";
						}
						setText(tempKey);
					}
				}
				if (keyCode == KeyEvent.VK_SHIFT)
					mods.remove("Shift");
				if (keyCode == KeyEvent.VK_CONTROL)
					mods.remove("Control");
				if (keyCode == KeyEvent.VK_ALT)
					mods.remove("Alt");
				if (mods.size() == 0)
					getParent().requestFocusInWindow();
			}
		});
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
		hotkey.setKeyCode(tempKey);
	}

}
