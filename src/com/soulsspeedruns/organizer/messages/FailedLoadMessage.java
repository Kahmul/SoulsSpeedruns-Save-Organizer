package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import jiconfont.IconCode;
import jiconfont.icons.Iconic;


/**
 * Short description.
 * <p>
 * Long description.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 14 Dec 2023
 */
public class FailedLoadMessage extends AbstractMessage
{

	private static final long serialVersionUID = -650759122764314062L;
	
	private static final String MESSAGE = "LOAD FAILED";
	private static final IconCode ICON = Iconic.CHECK;
	private static final Color COLOR = Color.decode("0xee6a5c");


	protected FailedLoadMessage()
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
	protected int getIconSize() {
		return 30;
	}


	@Override
	protected Color getColor()
	{
		return COLOR;
	}

}
