package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import jiconfont.IconCode;
import jiconfont.icons.Elusive;


/**
 * Short description.
 * <p>
 * Long description.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 13 Apr 2018
 */
public class SuccessfulRefreshMessage extends AbstractMessage
{

	private static final long serialVersionUID = -2151979076153299908L;

	private static final String MESSAGE = "REFRESH SUCCESSFUL";
	private static final IconCode ICON = Elusive.REPEAT;
	private static final Color COLOR = new Color(138, 43, 226);


	protected SuccessfulRefreshMessage()
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
