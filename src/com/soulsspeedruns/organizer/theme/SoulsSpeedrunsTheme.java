package com.soulsspeedruns.organizer.theme;

import java.util.Properties;

import javax.swing.UIDefaults;

import com.github.weisj.darklaf.properties.icons.IconResolver;
import com.github.weisj.darklaf.theme.Theme;
import com.github.weisj.darklaf.theme.info.PresetIconRule;
import com.github.weisj.darklaf.theme.spec.ColorToneRule;


public class SoulsSpeedrunsTheme extends Theme {

	@Override
    protected PresetIconRule getPresetIconRule() {
        return PresetIconRule.NONE;
    }

    @Override
    public String getPrefix() {
        return "soulsspeedruns";
    }

    @Override
    public String getName() {
        return "SoulsSpeedruns";
    }

    @Override
    protected String getResourcePath() {
        return "";
    }

    @Override
    protected Class<? extends Theme> getLoaderClass() {
        return SoulsSpeedrunsTheme.class;
    }

    @Override
    public ColorToneRule getColorToneRule() {
        return ColorToneRule.DARK;
    }

    @Override
    public void customizeUIProperties(final Properties properties, final UIDefaults currentDefaults,
            final IconResolver iconResolver) {
        super.customizeUIProperties(properties, currentDefaults, iconResolver);
        loadCustomProperties("ui", properties, currentDefaults, iconResolver);
    }

    @Override
    public boolean supportsCustomAccentColor() {
        return true;
    }

    @Override
    public boolean supportsCustomSelectionColor() {
        return true;
    }

    @Override
    public void customizeIconTheme(final Properties properties, final UIDefaults currentDefaults,
            final IconResolver iconResolver) {
//        loadCustomProperties("icons_adjustments", properties, currentDefaults, iconResolver);
    }

}
