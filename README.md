# <img src="https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/blob/develop/src/com/soulsspeedruns/organizer/resources/SoulsSpeedrunsLogo32.png" width="28px"/> SoulsSpeedruns - Save Organizer

The SoulsSpeedruns - Save Organizer is a tool designed to manage savefiles for Dark Souls, Dark Souls Remastered, Dark Souls II, Dark Souls II: Scholar of the First Sin, Dark Souls III, Sekiro and Elden Ring.

<p align="center">
    <img src="https://github.com/Kahmul/SoulsSpeedruns-Save-Organizer/blob/develop/docs/images/OrganizerOverview.png"/>
</p>

## Features

- Manage your savefiles for each supported game, loading or creating them with the click of a button.
- Create profiles for each game to group your savefiles for e.g. speedrun categories. You only see the savefiles assigned to the current profile at any given time.
- Switch the game's savefile to read-only and back with the click of a button for convenient practice without having to keep loading a savefile (not supported by DS1R).
- Support for global hotkeys.
- Support for Dark Souls, Dark Souls Remastered, Dark Souls II, Dark Souls II: Scholar of the First Sin, Dark Souls III, Sekiro and Elden Ring.

⚠️ **This application is primarily intended for speedrunners or challenge runners. There is little to no safe-guarding for keeping your savefiles if you modify or delete them. If you wish to use this program for backing up your casual playthroughs, make sure you know what you are doing and ideally make a separate backup of your savefiles elsewhere in case you mess up!** ⚠️

## Requirements

- Java JRE 8 or higher

## Download

[Latest Release](https://github.com/Kahmul/SpeedSouls-Save-Organizer/releases)


## Getting Started

To get started using the save organizer, follow these steps:

1. Start the application, press **Edit Profiles** in the top right.
2. In the **Profile Configuration** window choose the game you wish to create savefiles for at the top.
3. Click **Browse** under 'Location of Savefile' and navigate to the game's savefile (e.g. for Dark Souls this would be under "<User>/Documents/NBGI/DarkSouls/DRAKS0005.sl2").
4. The application will ask you if you wish to store your savefiles in the same directory where the game's savefile is stored. You can either agree or choose a different one under 'Profiles Directory'. If you already have existing profiles on your PC, you should point the organizer to that directory in this step.
5. Press **New** to create a new profile for your game. Name it whatever you'd like, e.g. the name of the category you wish to run.
6. Once you are done creating your profiles you can close the **Profile Configuration** window.
7. Back in the main window you can now choose the game and your profile(s) at the top.
8. Start creating savefiles by pressing **Import Savestate**, or **Rightclick > Add Folder** to create folders within your profiles. Savefiles will be imported into the selected folder.
9. *(Optional)* Click the cog button in the bottom right to access settings. You can enable global hotkeys there to conveniently work with the organizer during practice.

## Creating/Loading Savefiles

Due to the how the different FROMSoftware games work, there is a few things to consider when creating and loading savefiles.

- All characters are stored in the single savefile the game uses.
- Generally you want to quit out of the game before creating savefiles. This is not strictly necessary but it makes sure all progress has been saved to the savefile beforehand. Users more familiar with the games may want to force a save by simply opening/closing the start menu. You can then create a savefile during gameplay, or switch to read-only to repeat the upcoming section multiple times without having to quitout an extra time beforehand.
- Loading savefiles only works in the main menu. Loading a savefile in the middle of gameplay will do nothing as the game will simply overwrite it again the next time it saves.
- When loading a savefile in the main menu and looking at your characters, you may notice that nothing has changed. This is normal, the game only updates that info when the main menu is reloaded. If you choose a character slot, the corresponding character from the loaded savefile will be nonetheless correctly loaded.

## Planned Features

- Editing savefiles themselves to adjust stats/items (likely only for DS1)
- Bundling a JRE with the organizer to not require a separate JRE install
- Allowing users to add support for other games to the organizer themselves

## Troubleshooting

- Make sure you use Java JRE 8.
- If you have problems starting up the program after you've already been using it there might be conflicting data between the registry entries and the actual data. In this case it might help to remove the registry entries of the Save Organizer (the following steps are for Windows):

        1. Press Windows + R.
        2. Enter "regedit".
        3. Navigate to "HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\soulsspeedruns\organizer\prefs".
        4. Delete all entries.

## Credits

- johndisandonato for adding the 'Highlight Previous/Next Savefile' hotkeys
