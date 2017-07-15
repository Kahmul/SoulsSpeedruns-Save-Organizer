package com.speedsouls.organizer.messages;


import java.awt.Color;

import jiconfont.IconCode;
import jiconfont.icons.Iconic;


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

	private static final long serialVersionUID = 3917031603377354547L;

	private static final String MESSAGE = "IMPORT SUCCESSFUL";
	private static final IconCode ICON = Iconic.CURVED_ARROW;
	private static final Color COLOR = new Color(30, 144, 255);


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
