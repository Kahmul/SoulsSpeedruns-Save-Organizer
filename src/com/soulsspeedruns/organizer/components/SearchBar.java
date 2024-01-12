package com.soulsspeedruns.organizer.components;


import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.weisj.darklaf.ui.text.DarkTextFieldUI;
import com.github.weisj.darklaf.ui.text.DarkTextUI;
import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * Searchbar.
 * <p>
 * Allows the user to send search requests to the SaveList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 18 May 2016
 */
public class SearchBar extends JTextField
{

	public static final String DEFAULT_TEXT = "Search...";
	private static final int SEARCH_DELAY = 500;

	private Timer searchDelayTimer;
	private TimerTask searchTask;


	/**
	 * Creates a new searchbar.
	 */
	public SearchBar()
	{
		super(50);

		searchDelayTimer = new Timer(true);

		putClientProperty(DarkTextFieldUI.KEY_VARIANT, DarkTextFieldUI.VARIANT_SEARCH);
		putClientProperty(DarkTextFieldUI.KEY_SHOW_CLEAR, true);
		putClientProperty(DarkTextUI.KEY_DEFAULT_TEXT, DEFAULT_TEXT);
		
		getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				rescheduleSearch();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				rescheduleSearch();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				rescheduleSearch();
			}
		});
	}
	
	private void rescheduleSearch()
	{
		if (searchTask != null)
			searchTask.cancel();
		searchTask = new TimerTask() {

			@Override
			public void run()
			{
				OrganizerManager.fireSearchRequestedEvent(getText().trim());
			}
		};
		searchDelayTimer.schedule(searchTask, SEARCH_DELAY);
	}

}
