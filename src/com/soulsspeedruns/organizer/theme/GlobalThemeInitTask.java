package com.soulsspeedruns.organizer.theme;

import javax.swing.UIDefaults;
import javax.swing.plaf.InsetsUIResource;

import com.github.weisj.darklaf.task.DefaultsInitTask;
import com.github.weisj.darklaf.theme.Theme;

public class GlobalThemeInitTask implements DefaultsInitTask
{

	@Override
	public void run(Theme currentTheme, UIDefaults defaults)
	{
		defaults.put("Button.rollover", true);
		defaults.put("ComboBox.squareButton", true);
		defaults.put("ComboBox.valueInsets", new InsetsUIResource(3, 4, 4, 4));
		defaults.put("MenuItem.insets", new InsetsUIResource(3, 2, 3, 4));
	}

}
