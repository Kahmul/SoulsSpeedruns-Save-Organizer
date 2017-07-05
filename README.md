# <img src="https://github.com/Kahmul/SpeedSouls-Save-Organizer/blob/master/src/com/speedsouls/organizer/resources/SpeedSoulsIcon.png"/> SpeedSouls - Save Organizer

The SpeedSouls - Save Organizer is a tool designed to manage savefiles for Dark Souls, Dark Souls II, Dark Souls II: Scholar of the First Sin and Dark Souls III. 

## Features



## Requirements

- Java JRE 8 or higher

## Download

[Latest Release](https://github.com/Kahmul/SpeedSouls-Save-Organizer/releases)


## Getting Started

In order to use the program and make savefiles you first have to set it up accordingly. Here is a step by step guide on how to achieve this:

    1. Once you started the program, press 'Edit Profiles'.

    2. In the window that now opens you have to choose the right tab depending on which game you want to make savefiles for.

    3. Click 'Browse' and navigate to the directory where the game stores its savefile (e.g. for Dark Souls this would be "<User>/Documents/NBGI/DarkSouls/<SomeNumbers>").

    4. Confirm the directory so that the path shows up in the textfield next to the button.

    5. Now press 'New' to create a new profile for your game, name it whatever you'd like.

    6. Now you can close the 'Profile Configuration' window.

    7. Back in the main menu you can now choose the game and the profile you just set up and start making savefiles.


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

- 'Replace Savefile'-functionality
- Fix various UI issues
- Allow savefiles to be stored on a different directory
- Cut/Paste functionality
- Dragging and dropping multiple saves at once


## Troubleshooting

- Make sure you use Java JRE 8.
- Make sure your _JAVA_OPTIONS environment variable is set to proper values.
- If you have problems starting up the program after you've already been using it there might be conflicting data between the registry entries and the actual data. In this case it might help to remove the registry entries of the Save Organizer (the following steps are for Windows):

        1. Press Windows + R.
        2. Enter "regedit".
        3. Navigate to "HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\speedsouls\organizer\prefs".
        4. Delete all entries.
