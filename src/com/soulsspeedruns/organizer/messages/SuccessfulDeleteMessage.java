package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import jiconfont.IconCode;
import jiconfont.icons.FontAwesome;


/**
 * Short description.
 * <p>
 * Long description.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 2 Mar 2018
 */
public class SuccessfulDeleteMessage extends AbstractMessage
{

	private static final long serialVersionUID = 3917031603377354547L;

	private static final String MESSAGE = "DELETE SUCCESSFUL";
	private static final IconCode ICON = FontAwesome.CHECK;
	private static final Color COLOR = Color.decode("0xea3622");


	protected SuccessfulDeleteMessage()
	{
		super();
	}


	@Override
	protected String getMessage()
	{
		return MESSAGE;
	}


	@Override
	protected IconCode getIcon()
	{
		return ICON;
	}


	@Override
	protected Color getColor()
	{
		return COLOR;
	}

}
