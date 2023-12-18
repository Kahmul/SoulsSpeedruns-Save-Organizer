package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import jiconfont.IconCode;
import jiconfont.icons.Elusive;


/**
 * SuccessfulLoadMessage.
 * <p>
 * AbstractMessage class implementation for a Load message.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 15 Jul 2017
 */
public class SuccessfulLoadMessage extends AbstractMessage
{

	private static final long serialVersionUID = 7321956095930505656L;

	private static final String MESSAGE = "LOAD SUCCESSFUL";
	private static final IconCode ICON = Elusive.REPEAT;
	private static final Color COLOR = Color.decode("0x2c9558");


	protected SuccessfulLoadMessage()
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
