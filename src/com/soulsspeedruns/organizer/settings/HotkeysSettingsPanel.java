package com.soulsspeedruns.organizer.settings;


import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.soulsspeedruns.organizer.hotkeys.GlobalHotkey;


/**
 * HotkeysSettingsPanel.
 * <p>
 * Contains the textfields that allow the user to change the global hotkeys.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 3 Jun 2016
 */
public class HotkeysSettingsPanel extends JPanel
{

	private HotkeyTextField[] fields;


	/**
	 * Creates a new hotkeys settings panel.
	 */
	protected HotkeysSettingsPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		Component glue = Box.createHorizontalStrut(50);
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(glue));

		GroupLayout.ParallelGroup hLabelGroup = layout.createParallelGroup();
		GroupLayout.ParallelGroup hFieldGroup = layout.createParallelGroup();

		GlobalHotkey[] hotkeys = GlobalHotkey.values();
		fields = new HotkeyTextField[hotkeys.length];

		for (int i = 0; i < hotkeys.length; i++)
		{
			JLabel label = new JLabel(hotkeys[i].getCaption());
			fields[i] = new HotkeyTextField(hotkeys[i]);

			hLabelGroup.addComponent(label);
			hFieldGroup.addComponent(fields[i]);
			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(label).addComponent(fields[i]));
		}

		hGroup.addGroup(hLabelGroup);
		hGroup.addGroup(layout.createParallelGroup().addComponent(glue));
		hGroup.addGroup(hFieldGroup);

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		setLayout(layout);
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Global Hotkeys");
		setBorder(border);
	}


	/**
	 * Applies the changes to the hotkey shortcut.
	 */
	protected void applyChanges()
	{
		for (HotkeyTextField hotkeyTextField : fields)
		{
			hotkeyTextField.saveChangesToHotkey();
		}
	}

}