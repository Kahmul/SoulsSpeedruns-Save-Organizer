package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import javax.swing.Icon;

import jiconfont.icons.Iconic;
import jiconfont.swing.IconFontSwing;


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

	private static final String MESSAGE = "LOAD FAILED";
	private static final Color COLOR = Color.decode("0xee6a5c");
	private static final int ICON_SIZE = 22;
	private static final Icon ICON = IconFontSwing.buildIcon(Iconic.CHECK, ICON_SIZE, COLOR);


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
