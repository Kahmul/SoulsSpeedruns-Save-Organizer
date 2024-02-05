/**
 * 
 */
package com.soulsspeedruns.organizer.games;


import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.OrganizerManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;
import com.soulsspeedruns.organizer.savelist.Save;


/**
 * GameAppendageEditorWindow.
 * <p>
 * Base class for the window that allows manually editing appended date for savefiles.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 5 Feb 2024
 */
public abstract class GameAppendageEditorWindow extends JDialog
{

	private Map<String, String> saveValuesMap;

	private Save save;


	protected GameAppendageEditorWindow(Save save)
	{
		super(OrganizerManager.getMainWindow(), save.hasAppendedData() ? "Edit Appended Data" : "Add Appended Data",
				Dialog.ModalityType.APPLICATION_MODAL);

		this.save = save;

		saveValuesMap = GameAppendageHandler.getValuesMapFromAppendedData(save.getAppendedData());

		initLayout();
		initProperties();
	}


	/**
	 * Creates the layout for the window.
	 */
	protected abstract void initLayout();


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
					SwingUtilities.updateComponentTreeUI(GameAppendageEditorWindow.this);
				});
				SettingsManager.setGlobalHotkeysEnabled(false, false);
			}


			@Override
			public void windowClosing(WindowEvent e)
			{
				getSave().saveAppendedData(getDataFromComponents());

				SettingsManager.setGlobalHotkeysEnabled(true, false);
			}
		});
	}


	/**
	 * The save this window was opened on.
	 * 
	 * @return the save that is being edited
	 */
	public Save getSave()
	{
		return save;
	}


	/**
	 * Returns the appended data of the save as a key-values map.
	 * 
	 * @return the map of key-value pairs of the appended date for the save
	 */
	public Map<String, String> getKeysValuesMapForSave()
	{
		return saveValuesMap;
	}


	/**
	 * Retrieves all the relevant values from the UI components and adds them up in a data string.
	 * 
	 * @return the data to be appended to the end of the savefile as a string
	 */
	protected abstract String getDataFromComponents();

}
