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

## Requirements

- Java JRE 8 or higher

## Download

[Latest Release](https://github.com/Kahmul/SpeedSouls-Save-Organizer/releases)


## Getting Started

To get started using the save organizer, take the following steps:

1. Start the application, press **Edit Profiles** in the top right.
2. In the **Profile Configuration** window choose the game you wish to create savefiles for at the top.
3. Click **Browse** under 'Location of Savefile' and navigate to the game's savefile (e.g. for Dark Souls this would be under "<User>/Documents/NBGI/DarkSouls/DRAKS0005.sl2").
4. The application will ask you if you wish to store your savefiles in the same directory where the game's savefile is stored. You can either agree or choose a different one under 'Profiles Directory'. If you already have existing profiles on your PC, you should point the organizer to that directory in this step.
5. Press **New** to create a new profile for your game. Name it whatever you'd like, e.g. the name of the category you wish to run.
6. After creating the profiles you wished to create, you can close the **Profile Configuration** window.
7. Back in the main view, you can now choose the game and the profile(s) at the top.
8. Start creating savefiles by pressing **Import Savestate**, or **Rightclick > Add Folder** to create folders within your profiles.

## Creating/Loading Savefiles

In order to make savefiles with the Save Organizer you have to first set it up as described in the Getting Started section. Follow these steps once you've done that:

1. Make sure you are not currently in-game with any of your characters. That means either in the Main Menu or that the game is closed.

2. If you want to make a savefile in a folder, create one by right-clicking the list and pressing 'Add Folder'. Then select the folder so that the next savefile will be put into it.

3. Press 'Import current savefile' to make a copy of the current game's savefile and put it into the list. If you have a folder or one of its subcontents selected the savefile will be imported into that folder.

4. You have now created a copy of the save with all your characters and their current state. Name it however you like.

5. If you wish to put the savefile into a different folder just select it and drag and drop it into a different folder. You can do this with entire folders as well, it doesn't have to be a single save.

6. If you wish to reload the savefile, simply make sure that you are out of the game with any characters (so either Main Menu or not in the game at all), and press the 'Load selected savefile' button. This will overwrite the game's current savefile with the save you've selected.

7. If you go into the load screen menu now you may notice that the characters will not show the info according to the save you just loaded unless you restart the game. This is simply because the game only checks for that info on start-up. If you load any of the characters though it will now load the correct one.


## Planned Features

- None at the moment

## Troubleshooting

- Make sure you use Java JRE 8.
- Make sure your _JAVA_OPTIONS environment variable is set to proper values.
- If you have problems starting up the program after you've already been using it there might be conflicting data between the registry entries and the actual data. In this case it might help to remove the registry entries of the Save Organizer (the following steps are for Windows):

        1. Press Windows + R.
        2. Enter "regedit".
        3. Navigate to "HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\soulsspeedruns\organizer\prefs".
        4. Delete all entries.

## Credits

- johndisandonato for adding the 'select previous/next savefile' hotkeys
