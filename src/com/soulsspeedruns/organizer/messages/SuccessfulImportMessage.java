package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import javax.swing.Icon;

import com.soulsspeedruns.organizer.data.OrganizerManager;


/**
 * SuccessfulImportMessage.
 * <p>
 * AbstractMessage class implementation for an Import message.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jul 2017
 */
public class SuccessfulImportMessage extends AbstractMessage
{

	private static final String MESSAGE = "IMPORT SUCCESSFUL";
	private static final Icon ICON = OrganizerManager.importIcon24;
	private static final Color COLOR = Color.decode("0x1d6fbe");


	protected SuccessfulImportMessage()
	{
		super();
	}


	@Override
	protected String getMessage()
	{
		return MESSAGE;
	}


	@Override
	protected Icon getIcon()
	{
		return ICON;
	}
	

	@Override
	protected int getIconSize() {
		return 22;
	}
	

	@Override
	protected Color getColor()
	{
		return COLOR;
	}

}
