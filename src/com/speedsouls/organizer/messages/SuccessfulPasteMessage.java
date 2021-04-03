package com.speedsouls.organizer.messages;

import jiconfont.IconCode;
import jiconfont.icons.Elusive;

import java.awt.*;

public class SuccessfulPasteMessage extends AbstractMessage {
    private static final long serialVersionUID = 7321946695930305656L;

    private static final String MESSAGE = "PASTE SUCCESSFUL";
    private static final IconCode ICON = Elusive.BRUSH;
    private static final Color COLOR = new Color(39, 174, 96);


    public SuccessfulPasteMessage()
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
