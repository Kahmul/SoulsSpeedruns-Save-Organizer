package com.soulsspeedruns.organizer.messages;


import java.awt.Color;

import javax.swing.Icon;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


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

	private static final String MESSAGE = "DELETE SUCCESSFUL";
	private static final Color COLOR = Color.decode("0xea3622");
	private static final int ICON_SIZE = 22;
	private static final Icon ICON = IconFontSwing.buildIcon(FontAwesome.CHECK, ICON_SIZE, COLOR);


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
