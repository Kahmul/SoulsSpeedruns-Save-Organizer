/**
 * 
 */
package com.soulsspeedruns.organizer.games.ds1;


import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.soulsspeedruns.organizer.components.ImageBackgroundPanel;
import com.soulsspeedruns.organizer.games.GameAppendageHandler;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.savelist.Save;


/**
 * <ShortDescription>
 * <p>
 * <LongDescription>
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 4 Feb 2024
 */
public class DS1AppendageEditorWindow extends JDialog
{

	private Map<String, JTextField> equipSlotTextFieldMap;
	private Map<String, String> saveValuesMap;

	private Save save;


	public DS1AppendageEditorWindow(Save save)
	{
		super(OrganizerManager.getMainWindow(), save.hasAppendedData() ? "Edit Appended Data" : "Add Appended Data",
				Dialog.ModalityType.APPLICATION_MODAL);

		this.save = save;

		saveValuesMap = GameAppendageHandler.getValuesMapFromAppendedData(save.getAppendedData());
		equipSlotTextFieldMap = new HashMap<>();

		initLayout();
		initProperties();
	}


	/**
	 * Sets the properties of the window.
	 */
	private void initProperties()
	{
		pack();
		setResizable(false);
		setLocationRelativeTo(OrganizerManager.getMainWindow());
		setIconImage(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_MEDIUM));
		setAlwaysOnTop(SettingsManager.isAlwaysOnTop());

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowOpened(WindowEvent e)
			{
				requestFocusInWindow();
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.updateComponentTreeUI(DS1AppendageEditorWindow.this);
				});
				SettingsManager.setGlobalHotkeysEnabled(false, false);
			}


			@Override
			public void windowClosing(WindowEvent e)
			{
				save.saveAppendedData(getDataFromTextFields());

				SettingsManager.setGlobalHotkeysEnabled(true, false);
			}
		});
	}


	/**
	 * Creates the layout for the window.
	 */
	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));

		guiPanel.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocusInWindow();
			}
		});

		guiPanel.add(createExplanationPanel());
		guiPanel.add(createEditIndicesPanel());
		guiPanel.add(createButtonPanel());
		
//		guiPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

		add(guiPanel);
	}


	private JPanel createButtonPanel()
	{
		JButton clearAllButton = new JButton("Clear All");
		clearAllButton.addActionListener(e -> {
			for (JTextField field : equipSlotTextFieldMap.values())
			{
				field.setText("");
			}
		});
		
		JButton fillEmptyButton = new JButton("Fill Empty With 0");
		fillEmptyButton.addActionListener(e -> {
			for (JTextField field : equipSlotTextFieldMap.values())
			{
				if(field.getText().equals(""))
					field.setText("0");
			}
		});
		
		JButton clearZeroButton = new JButton("Clear All 0s");
		clearZeroButton.addActionListener(e -> {
			for (JTextField field : equipSlotTextFieldMap.values())
			{
				if(field.getText().equals("0"))
					field.setText("");
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(fillEmptyButton);
		buttonPanel.add(clearZeroButton);
		buttonPanel.add(clearAllButton);
		return buttonPanel;
	}


	private JPanel createExplanationPanel()
	{
		JPanel guiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JLabel label = new JLabel("<html>Here you can manually edit the default selection index of each equipment slot.<br>"
				+ "The indices determine which item the equipment list will default to when opening each slot.<br>"
				+ "When the save is loaded, the values for each slot will be loaded into the game's memory.<br>"
				+ "0 is the first item in the list, 1 the second, and so on. Indices that are too large will default to the last item.<br>"
				+ "If no value is set, the value that is currently in the game's memory will be left intact.</html>");

		guiPanel.add(label);

		return guiPanel;
	}


	private JPanel createEditIndicesPanel()
	{
		ImageBackgroundPanel backgroundPanel = new ImageBackgroundPanel(IconsAndFontsManager.getDS1EquipMenuBackground());

		backgroundPanel.setLayout(null);

		createWeaponTextFields(backgroundPanel);
		createArrowTextFields(backgroundPanel);
		createArmorTextFields(backgroundPanel);
		createRingTextFields(backgroundPanel);
		createHotbarTextFields(backgroundPanel);

		return backgroundPanel;
	}


	private void createWeaponTextFields(ImageBackgroundPanel backgroundPanel)
	{
		JTextField fieldRH1 = createEquipSlotTextField(DS1.getEquipSlots().get(1), 98, 47);
		backgroundPanel.add(fieldRH1);

		JTextField fieldRH2 = createEquipSlotTextField(DS1.getEquipSlots().get(3), 173, 47);
		backgroundPanel.add(fieldRH2);

		JTextField fieldLH1 = createEquipSlotTextField(DS1.getEquipSlots().get(0), 98, 144);
		backgroundPanel.add(fieldLH1);

		JTextField fieldLH2 = createEquipSlotTextField(DS1.getEquipSlots().get(2), 173, 144);
		backgroundPanel.add(fieldLH2);
	}


	private void createArrowTextFields(ImageBackgroundPanel backgroundPanel)
	{
		JTextField fieldArrow1 = createEquipSlotTextField(DS1.getEquipSlots().get(4), 294, 135);
		backgroundPanel.add(fieldArrow1);

		JTextField fieldArrow2 = createEquipSlotTextField(DS1.getEquipSlots().get(6), 349, 135);
		backgroundPanel.add(fieldArrow2);

		JTextField fieldBolt1 = createEquipSlotTextField(DS1.getEquipSlots().get(5), 464, 135);
		backgroundPanel.add(fieldBolt1);

		JTextField fieldBolt2 = createEquipSlotTextField(DS1.getEquipSlots().get(7), 521, 135);
		backgroundPanel.add(fieldBolt2);
	}


	private void createArmorTextFields(ImageBackgroundPanel backgroundPanel)
	{
		JTextField fieldHead = createEquipSlotTextField(DS1.getEquipSlots().get(8), 99, 238);
		backgroundPanel.add(fieldHead);

		JTextField fieldChest = createEquipSlotTextField(DS1.getEquipSlots().get(9), 175, 238);
		backgroundPanel.add(fieldChest);

		JTextField fieldArms = createEquipSlotTextField(DS1.getEquipSlots().get(10), 250, 238);
		backgroundPanel.add(fieldArms);

		JTextField fieldLegs = createEquipSlotTextField(DS1.getEquipSlots().get(11), 325, 238);
		backgroundPanel.add(fieldLegs);
	}


	private void createRingTextFields(ImageBackgroundPanel backgroundPanel)
	{
		JTextField fieldRing1 = createEquipSlotTextField(DS1.getEquipSlots().get(13), 464, 236);
		backgroundPanel.add(fieldRing1);

		JTextField fieldRing2 = createEquipSlotTextField(DS1.getEquipSlots().get(14), 521, 236);
		backgroundPanel.add(fieldRing2);
	}


	private void createHotbarTextFields(ImageBackgroundPanel backgroundPanel)
	{
		JTextField fieldHotbar1 = createEquipSlotTextField(DS1.getEquipSlots().get(15), 294, 38);
		backgroundPanel.add(fieldHotbar1);

		JTextField fieldHotbar2 = createEquipSlotTextField(DS1.getEquipSlots().get(16), 349, 38);
		backgroundPanel.add(fieldHotbar2);

		JTextField fieldHotbar3 = createEquipSlotTextField(DS1.getEquipSlots().get(17), 407, 38);
		backgroundPanel.add(fieldHotbar3);

		JTextField fieldHotbar4 = createEquipSlotTextField(DS1.getEquipSlots().get(18), 464, 38);
		backgroundPanel.add(fieldHotbar4);

		JTextField fieldHotbar5 = createEquipSlotTextField(DS1.getEquipSlots().get(19), 521, 38);
		backgroundPanel.add(fieldHotbar5);
	}


	/**
	 * Creates a new textfield for the given slot.
	 * 
	 * @param slotName the name of the equip slot
	 * @param x the x coordinate at the center of the textfield
	 * @param y the y coordinate at the center of the textfield
	 * @return the textfield
	 */
	private JTextField createEquipSlotTextField(String slotName, int x, int y)
	{
		JTextField field = new JTextField(saveValuesMap.get(slotName), 2);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		
		int width = field.getPreferredSize().width;
		int height = field.getPreferredSize().height;
		field.setBounds(x - width/2, y - height/2, width, height);

		AbstractDocument document = (AbstractDocument) field.getDocument();
		document.setDocumentFilter(new DocumentFilter()
		{

			private int limit = 4;
			Pattern numbersOnlyRegex = Pattern.compile("[0-9]*");


			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
			{
				Matcher matcher = numbersOnlyRegex.matcher(text);
				if (!matcher.matches())
					return;

				int currentLength = fb.getDocument().getLength();
				int overLimit = (currentLength + text.length()) - limit - length;
				if (overLimit > 0)
				{
					text = text.substring(0, text.length() - overLimit);
				}

				super.replace(fb, offset, length, text, attrs);
			}
		});

		equipSlotTextFieldMap.put(slotName, field);

		return field;
	}


	private String getDataFromTextFields()
	{
		String data = "";

		for (Map.Entry<String, JTextField> entry : equipSlotTextFieldMap.entrySet())
		{
			String text = entry.getValue().getText();
			if (text.length() == 0)
				continue;
			
			if(text.contains("-"))
				text = "-1";

			data += GameAppendageHandler.createNewKeyValuePair(entry.getKey(), text);
		}

		return data;
	}
}
