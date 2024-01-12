package com.soulsspeedruns.organizer.theme;


import java.awt.Color;
import java.util.Properties;

import com.github.weisj.darklaf.task.DefaultsAdjustmentTask;
import com.github.weisj.darklaf.theme.Theme;


public class GlobalThemeAdjustmentTask implements DefaultsAdjustmentTask
{


	@Override
	public void run(Theme currentTheme, Properties properties)
	{
		// fixes the title pane text not being the proper font color with decorations enabled
		properties.put("textForegroundSecondary", properties.get("textForeground"));
		properties.put("borderThickness", 0);
		properties.put("arc", 0);
		properties.put("disabledIconColor",
				Theme.isDark(currentTheme) ? Color.decode("#495162") : Color.decode("#bfc0c1"));
	}

}
