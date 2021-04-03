package com.speedsouls.organizer.messages;

import jiconfont.IconCode;
import jiconfont.icons.Elusive;
import java.awt.*;

public class SuccessfulCopyMessage extends AbstractMessage {
    private static final long serialVersionUID = 7321956095930305656L;

    private static final String MESSAGE = "COPY SUCCESSFUL";
    private static final IconCode ICON = Elusive.PLUS_SIGN;
    private static final Color COLOR = new Color(39, 174, 96);


    public SuccessfulCopyMessage()
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
