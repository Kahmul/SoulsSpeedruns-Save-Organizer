package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import javax.swing.Icon;

import jiconfont.icons.Elusive;
import jiconfont.swing.IconFontSwing;


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

	private static final String MESSAGE = "REPLACE SUCCESSFUL";
	private static final Color COLOR = Color.decode("0xeb751c");
	private static final int ICON_SIZE = 20;
	private static final Icon ICON = IconFontSwing.buildIcon(Elusive.REFRESH, ICON_SIZE, COLOR);


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
	protected Icon getIcon()
	{
		return ICON;
	}
	

	@Override
	protected int getIconSize() {
		return ICON_SIZE;
	}


	@Override
	protected Color getColor()
	{
		return COLOR;
	}

}
