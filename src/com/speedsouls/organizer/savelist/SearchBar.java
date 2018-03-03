package com.speedsouls.organizer.savelist;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextField;

import com.speedsouls.organizer.data.OrganizerManager;


/**
 * Searchbar.
 * <p>
 * Allows the user to send search requests to the SaveList.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 18 May 2016
 */
public class SearchBar extends JTextField implements FocusListener, KeyListener
{

	private static final long serialVersionUID = -1608497657232185005L;

	public static final String DEFAULT_TEXT = "Search...";
	private static final int SEARCH_DELAY = 500;

	private Timer searchDelayTimer;
	private TimerTask searchTask;


	/**
	 * Creates a new searchbar.
	 */
	public SearchBar()
	{
		super(DEFAULT_TEXT, 50);

		searchDelayTimer = new Timer(true);

		addFocusListener(this);
		addKeyListener(this);
	}


	@Override
	public void focusGained(FocusEvent e)
	{
		if (getText().equals(DEFAULT_TEXT))
			setText("");
	}


	@Override
	public void focusLost(FocusEvent e)
	{
		if (getText().equals(""))
			setText(DEFAULT_TEXT);
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
	}


	@Override
	public void keyReleased(KeyEvent e)
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


	@Override
	public void keyTyped(KeyEvent e)
	{
	}

}
