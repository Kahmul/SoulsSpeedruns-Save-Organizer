package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import javax.swing.Icon;

import jiconfont.icons.Elusive;
import jiconfont.swing.IconFontSwing;


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

	private static final String MESSAGE = "REFRESH SUCCESSFUL";
	private static final Color COLOR = Color.decode("0x1d6fbe");
	private static final int ICON_SIZE = 22;
	private static final Icon ICON = IconFontSwing.buildIcon(Elusive.REPEAT, ICON_SIZE, COLOR);


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
