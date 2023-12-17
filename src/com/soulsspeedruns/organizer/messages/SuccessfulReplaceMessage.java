package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import jiconfont.IconCode;
import jiconfont.icons.Elusive;


/**
 * SuccessfulReplaceMessage.
 * <p>
 * AbstractMessage class implementation for a Replace message.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jul 2017
 */
public class SuccessfulReplaceMessage extends AbstractMessage
{

	private static final long serialVersionUID = 3917031603377354547L;

	private static final String MESSAGE = "REPLACE SUCCESSFUL";
	private static final IconCode ICON = Elusive.REFRESH;
	private static final Color COLOR = Color.decode("0xffb21d");


	protected SuccessfulReplaceMessage()
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
