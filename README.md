# <img src="https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/blob/develop/src/com/soulsspeedruns/organizer/images/SoulsSpeedrunsLogo32.png" width="28px"/> SoulsSpeedruns - Save Organizer

<p align="center">
    <b>Click the banner below to join the SoulsSpeedruns Discord!</b><br><br>
    <a href="https://discord.soulsspeedruns.com"><img src="https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/blob/develop/docs/images/SoulsSpeedruns_Banner.png" width="350px"/></a>
</p>

## Table of Contents

1. [Description](#description)
2. [Features](#features)  
3. [Requirements](#requirements)
4. [Download](#download)
5. [Getting Started](#getting-started) 
6. [Creating/Loading Savefiles](#creatingloading-savefiles)
7. [Planned Features](#planned-features)
8. [Troubleshooting](#troubleshooting)  
9. [Credits](#credits)

## Description

The SoulsSpeedruns - Save Organizer is a tool designed to manage savefiles for Dark Souls, Dark Souls Remastered, Dark Souls II, Dark Souls II: Scholar of the First Sin, Dark Souls III, Sekiro and Elden Ring. Support for other games can be added by the user.

<p align="center">
    <img src="https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/blob/develop/docs/images/OrganizerOverview1_6.png"/>
</p>

## Features

- Manage your savefiles for each supported game, loading or creating them with the click of a button.
- Create profiles for each game to group your savefiles for e.g. speedrun categories. You only see the savefiles assigned to the current profile at any given time.
- Switch the game's savefile to read-only and back with the click of a button for convenient practice without having to manually reload (not supported by DS1R).
- Support for global hotkeys.
- Support out of the box for Dark Souls, Dark Souls Remastered, Dark Souls II, Dark Souls II: Scholar of the First Sin, Dark Souls III, Sekiro and Elden Ring.
- Support for other games can be added by the user (for games that use a single file to store their save data like the Souls games).
- Multiple UI themes, including a SoulsSpeedruns theme in the style of the [SoulsSpeedruns wiki](https://soulsspeedruns.com/) dark mode.

⚠️ **This application is primarily intended for speedrunners or challenge runners. There is little to no safeguarding for keeping your savefiles if you modify or delete them. If you wish to use this program for backing up your first playthrough or similar, make sure you know what you are doing and ideally make a separate backup of your savefiles elsewhere in case you mess up!** ⚠️

## Requirements

- Java JRE 8 or higher (included in the bundled version)

## Download

[Latest Release](https://github.com/Kahmul/SpeedSouls-Save-Organizer/releases)

- **Windows version:** Runnable .exe file. Requires Java JRE 8+ to be installed on your system.
- **Bundled Windows version:** Runnable .exe file. Bundled with JRE 8.
- **Linux version:** Runnable .jar file. Run it via CLI <code>java -jar "SoulsSpeedruns - Save Organizer.jar"</code> or similar.

## Getting Started

To get started using the save organizer, follow these steps:

1. Start the application, press **Edit Games** in the top right.
2. In the **Games Configuration** window choose the game you wish to create savefiles for on the side.
3. Click **Browse** under 'Savefile Location' to choose the file the game uses to store its savedata. If you are choosing the savefile for a Souls game, the organizer will try to automatically find the savefile in its usual default location, so most users will just need to confirm the suggested selection. If you already tinkered with savefiles manually, make sure that you select the correct savefile the game uses. This can usually be verified by checking the date it was last modified.
4. The application will ask you if you wish to store your savefiles in the same directory where the game's savefile is stored. You can either agree or choose a different one under 'Profiles Directory'. If you already have existing profiles on your PC, you should point the organizer to that directory in this step.
5. Press **New** to create a new profile for your game. Name it whatever you'd like, e.g. the name of the category you wish to run.
6. Once you are done creating your profiles you can close the **Games Configuration** window.
7. Back in the main window you can now choose the game and your profile(s) at the top.
8. Start creating savefiles by pressing **Import Savestate**, or **Rightclick > Add Folder** to create folders within your profiles. Savefiles will be imported into the selected folder.
9. *(Optional)* Click the cog button in the bottom right to access settings. You can enable global hotkeys there to conveniently work with the organizer during practice, as well as change the UI theme of the organizer.
10. *(Optional)* If you wish to add support for other games besides the Souls games, click the + in the top left of the **Games Configuration** window. There you can create a custom game by giving it a name and telling the organizer what file name to expect for the savefile. Once created, the game functions like any other game in the organizer. You can adjust the order of the games via drag and drop if you wish to have your custom games higher up the list.

## Creating/Loading Savefiles

Due to the how the different FROMSoftware games work, there is a few things to consider when creating and loading savefiles.

- All characters are stored in the single savefile the game uses.
- Generally you want to quit out to the main menu before creating savefiles. This is not strictly necessary but it makes sure all progress has been saved to the savefile beforehand. Users more familiar with the games may want to force a save by e.g. simply opening/closing the start menu. You can then create a savefile during gameplay, or switch to read-only to repeat the upcoming section multiple times without having to quitout an extra time beforehand.
- Loading savefiles only works in the main menu. Loading a savefile in the middle of gameplay will do nothing as the game will simply overwrite it again the next time it saves.
- When loading a savefile in the main menu and looking at your characters, you may notice that nothing has changed. This is normal, the game only updates that info when the main menu is reloaded. If you choose a character slot, the corresponding character from the loaded savefile will be nonetheless correctly loaded.

## Savefile Locations

The organizer should automatically find your savefile when you open the respective file browser as outlined in [Getting Started](#getting-started), so you should not need to search for it.
If you tinkered with your savefiles manually in advance however, or are otherwise curious where the savefiles can be found, here are their default locations on Windows:

- Dark Souls: <code>C:\Users\\<User\>\Documents\NBGI\DarkSouls</code>
- Dark Souls Remastered: <code>C:\User\s\\<User>\Documents\NBGI\DARK SOULS REMASTERED</code>
- Dark Souls II: <code>C:\Users\\<User\>\AppData\Roaming\DarkSoulsII\\<YourSteamID\></code>
- Dark Souls III: <code>C:\Users\\<User\>\AppData\Roaming\DarkSoulsIII\\<YourSteamID\></code>
- Sekiro: <code>C:\Users\\<User\>\AppData\Roaming\Sekiro\\<YourSteamID\></code>
- Elden Ring: <code>C:\Users\\<User\>\AppData\Roaming\EldenRing\\<YourSteamID\></code>

## Planned Features

- Editing savefiles to adjust stats/items for characters (likely only for DS1)

## Troubleshooting

- You can join the [SoulsSpeedruns Discord](https://discord.soulsspeedruns.com) for troubleshooting help.
- Make sure you use Java JRE 8. You can use the bundled version for this.
- If you have problems starting up the program after you've already been using it there might be conflicting data between the registry entries and the actual data. In this case it might help to remove the registry entries of the Save Organizer (the following steps are for Windows):

        1. Press Windows + R.
        2. Enter "regedit".
        3. Navigate to "HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\soulsspeedruns\organizer\prefs".
        4. Delete all entries.

## Credits

- johndisandonato for adding the 'Highlight Previous/Next Savestate' hotkeys
