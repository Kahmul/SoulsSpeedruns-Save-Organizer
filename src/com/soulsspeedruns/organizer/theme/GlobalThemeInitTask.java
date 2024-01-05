package com.soulsspeedruns.organizer.theme;

import javax.swing.UIDefaults;

import com.github.weisj.darklaf.task.DefaultsInitTask;
import com.github.weisj.darklaf.theme.Theme;

public class GlobalThemeInitTask implements DefaultsInitTask
{

	@Override
	public void run(Theme currentTheme, UIDefaults defaults)
	{
		defaults.put("Button.arc", 3);
		defaults.put("ComboBox.arc", 3);
	}

}
