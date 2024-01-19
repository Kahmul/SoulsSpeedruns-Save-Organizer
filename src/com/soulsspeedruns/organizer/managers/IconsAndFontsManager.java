/**
 * 
 */
package com.soulsspeedruns.organizer.managers;


import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.Theme;

import jiconfont.icons.Elusive;
import jiconfont.icons.FontAwesome;
import jiconfont.icons.Iconic;
import jiconfont.swing.IconFontSwing;


/**
 * IconsAndFontsManager
 * <p>
 * Handles loading icons and fonts and offers methods to use the icons in the application
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 19 Jan 2024
 */
public class IconsAndFontsManager
{

	private static final String RESOURCE_PATH = "/com/soulsspeedruns/organizer/resources/";
	private static final String RESOURCE_READONLY_PATH = RESOURCE_PATH + "readonlyicon/";

	private static final String SOULSSPEEDRUNS_ICON = "soulsspeedruns";
	private static final String READ_ONLY_ICON = "readOnly";
	private static final String WRITABLE_ICON = "writable";
	private static final String IMPORT_ICON = "import";

	private static final String DARKMODE_ICON = "DarkMode";
	private static final String HOVER_ICON = "Hover";

	public static final String ICON_SIZE_SMALL = "Small";
	public static final String ICON_SIZE_MEDIUM = "Medium";
	public static final String ICON_SIZE_LARGE = "Large";

	private static Image soulsspeedrunsIconMedium;
	private static Image soulsspeedrunsIconSmall;
	private static Image soulsspeedrunsIconLarge;

	private static ImageIcon readOnlyIcon14;
	private static ImageIcon readOnlyIcon16;
	private static ImageIcon readOnlyIcon22;
	private static ImageIcon readOnlyIconHover22;

	private static ImageIcon readOnlyIconDarkMode14;
	private static ImageIcon readOnlyIconDarkMode16;
	private static ImageIcon readOnlyIconDarkMode22;
	private static ImageIcon readOnlyIconDarkModeHover22;

	private static ImageIcon writableIcon14;
	private static ImageIcon writableIcon16;
	private static ImageIcon writableIcon22;
	private static ImageIcon writableIconHover22;

	private static ImageIcon writableIconDarkMode14;
	private static ImageIcon writableIconDarkMode16;
	private static ImageIcon writableIconDarkMode22;
	private static ImageIcon writableIconDarkModeHover22;

	private static ImageIcon discordIcon;
	private static ImageIcon frankerZIcon;
	private static ImageIcon importIcon;
	private static ImageIcon importIcon24;

	private static HashMap<String, ImageIcon> icons;
	private static HashMap<String, Image> images;


	public static void initialize() throws IOException
	{
		loadIcons();
		loadMappings();
		loadFonts();
	}


	private static void loadIcons() throws IOException
	{
		soulsspeedrunsIconMedium = ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "SoulsSpeedrunsLogo32.png"));
		soulsspeedrunsIconSmall = ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "SoulsSpeedrunsLogo16.png"));
		soulsspeedrunsIconLarge = ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "SoulsSpeedrunsLogo100.png"));

		readOnlyIcon14 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIcon14.png")));
		readOnlyIcon16 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIcon16.png")));
		readOnlyIcon22 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIcon22.png")));
		readOnlyIconHover22 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIconHover22.png")));

		readOnlyIconDarkMode14 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIconDarkMode14.png")));
		readOnlyIconDarkMode16 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIconDarkMode16.png")));
		readOnlyIconDarkMode22 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIconDarkMode22.png")));
		readOnlyIconDarkModeHover22 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "ReadOnlyIconDarkModeHover22.png")));

		writableIcon14 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIcon14.png")));
		writableIcon16 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIcon16.png")));
		writableIcon22 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIcon22.png")));
		writableIconHover22 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIconHover22.png")));

		writableIconDarkMode14 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIconDarkMode14.png")));
		writableIconDarkMode16 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIconDarkMode16.png")));
		writableIconDarkMode22 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIconDarkMode22.png")));
		writableIconDarkModeHover22 = new ImageIcon(
				ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_READONLY_PATH + "WritableIconDarkModeHover22.png")));

		discordIcon = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "DiscordLogo.png")));
		frankerZIcon = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "FrankerZ.png")));
		importIcon = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "ImportIcon.png")));
		importIcon24 = new ImageIcon(ImageIO.read(IconsAndFontsManager.class.getResourceAsStream(RESOURCE_PATH + "ImportIcon24.png")));
	}


	private static void loadFonts()
	{
		IconFontSwing.register(FontAwesome.getIconFont());
		IconFontSwing.register(Elusive.getIconFont());
//		IconFontSwing.register(Entypo.getIconFont());
		IconFontSwing.register(Iconic.getIconFont());
//		IconFontSwing.register(Typicons.getIconFont());
	}


	private static void loadMappings()
	{
		icons = new HashMap<>();
		images = new HashMap<>();

		icons.put(READ_ONLY_ICON + ICON_SIZE_SMALL, readOnlyIcon14);
		icons.put(READ_ONLY_ICON + ICON_SIZE_SMALL + DARKMODE_ICON, readOnlyIconDarkMode14);
		
		icons.put(READ_ONLY_ICON + ICON_SIZE_MEDIUM, readOnlyIcon16);
		icons.put(READ_ONLY_ICON + ICON_SIZE_MEDIUM + DARKMODE_ICON, readOnlyIconDarkMode16);

		icons.put(READ_ONLY_ICON + ICON_SIZE_LARGE, readOnlyIcon22);
		icons.put(READ_ONLY_ICON + ICON_SIZE_LARGE + HOVER_ICON, readOnlyIconHover22);
		icons.put(READ_ONLY_ICON + ICON_SIZE_LARGE + DARKMODE_ICON, readOnlyIconDarkMode22);
		icons.put(READ_ONLY_ICON + ICON_SIZE_LARGE + DARKMODE_ICON + HOVER_ICON, readOnlyIconDarkModeHover22);

		icons.put(WRITABLE_ICON + ICON_SIZE_SMALL, writableIcon14);
		icons.put(WRITABLE_ICON + ICON_SIZE_SMALL + DARKMODE_ICON, writableIconDarkMode14);
		
		icons.put(WRITABLE_ICON + ICON_SIZE_MEDIUM, writableIcon16);
		icons.put(WRITABLE_ICON + ICON_SIZE_MEDIUM + DARKMODE_ICON, writableIconDarkMode16);

		icons.put(WRITABLE_ICON + ICON_SIZE_LARGE, writableIcon22);
		icons.put(WRITABLE_ICON + ICON_SIZE_LARGE + HOVER_ICON, writableIconHover22);
		icons.put(WRITABLE_ICON + ICON_SIZE_LARGE + DARKMODE_ICON, writableIconDarkMode22);
		icons.put(WRITABLE_ICON + ICON_SIZE_LARGE + DARKMODE_ICON + HOVER_ICON, writableIconDarkModeHover22);

		icons.put(IMPORT_ICON + ICON_SIZE_SMALL, importIcon);
		icons.put(IMPORT_ICON + ICON_SIZE_LARGE, importIcon24);

		images.put(SOULSSPEEDRUNS_ICON + ICON_SIZE_SMALL, soulsspeedrunsIconSmall);
		images.put(SOULSSPEEDRUNS_ICON + ICON_SIZE_MEDIUM, soulsspeedrunsIconMedium);
		images.put(SOULSSPEEDRUNS_ICON + ICON_SIZE_LARGE, soulsspeedrunsIconLarge);
	}


	public static ImageIcon getReadOnlyIcon(String size, boolean isHovering)
	{
		String iconName = READ_ONLY_ICON + size;

		if (Theme.isDark(LafManager.getInstalledTheme()))
			iconName += DARKMODE_ICON;
		if (isHovering)
			iconName += HOVER_ICON;

		return icons.get(iconName);
	}


	public static ImageIcon getWritableIcon(String size, boolean isHovering)
	{
		String iconName = WRITABLE_ICON + size;

		if (Theme.isDark(LafManager.getInstalledTheme()))
			iconName += DARKMODE_ICON;
		if (isHovering)
			iconName += HOVER_ICON;
		
		return icons.get(iconName);
	}


	public static ImageIcon getDiscordIcon()
	{
		return discordIcon;
	}


	public static Image getSoulsSpeedrunsImage(String size)
	{
		return images.get(SOULSSPEEDRUNS_ICON + size);
	}


	public static ImageIcon getFrankerZ()
	{
		return frankerZIcon;
	}


	public static ImageIcon getImportIcon(String size)
	{
		return icons.get(IMPORT_ICON + size);
	}

}
