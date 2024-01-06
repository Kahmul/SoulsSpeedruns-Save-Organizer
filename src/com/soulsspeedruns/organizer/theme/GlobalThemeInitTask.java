package com.soulsspeedruns.organizer.theme;

import java.awt.Font;

import javax.swing.UIDefaults;
import javax.swing.plaf.FontUIResource;
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
//		defaults.put("MenuItem.margin", new InsetsUIResource(4, -10, 4, 4));
//		defaults.put("CheckBoxMenuItem.iconTextGap", 2);
//		defaults.put("Button.font", new FontUIResource(defaults.getFont("Button.font").deriveFont(11f)));
//		defaults.put("ComboBox.font", new FontUIResource(defaults.getFont("ComboBox.font").deriveFont(12f)));
	}

}
