package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import javax.swing.Icon;

import jiconfont.icons.Elusive;
import jiconfont.swing.IconFontSwing;


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

	private static final String MESSAGE = "LOAD SUCCESSFUL";
	private static final Color COLOR = Color.decode("0x2c9558");
	private static final int ICON_SIZE = 22;
	private static final Icon ICON = IconFontSwing.buildIcon(Elusive.REPEAT, ICON_SIZE, COLOR);


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
