# Tribute to Paganitzu

**Tribute to Paganitzu** is an open-source, Java-based reimplementation of the shareware episode of the classic Apogee game "Paganitzu" created by Keith Schuler in 1991 and published by [Apogee](https://legacy.3drealms.com/paganitzu/). *Tribute* started out as a small tech demo for the [Java-GI](https://jwharm.github.io/java-gi) and [Cairo language bindings](https://github.com/jwharm/cairo-java-bindings), but I gradually added all features necessary to play the entire Paganitzu shareware game.

Technically, *Tribute* is just a game engine and not a full game: the data files from the Paganitzu shareware episode are used for the levels and graphics. The game has been built with Java and the open-source, cross-platform UI libraries Gtk4, LibAdwaita and Cairo.

## Installation

Currently you'll need to clone the GitHub repository (or download the contents as a zip archive) and execute `./gradlew run`. Installers are planned to be published sometime later.

## Game engine design

The game engine consists of four packages: `engine`, `ui`, `io` and `transitions`. The `engine` package contains the core gameplay functionality. The `GameSession` class represents the current gameplay session, with the current level (room) in a `Room` object. A `Room` is basically a grid of tiles that is continuously updated during gameplay. Besides the room, some global game-state variables (lives, score, keys) are kept in the `GameState` class. Every 100 milliseconds, the `GameSession.updateState()` method is run. When a cursor key was pressed, it will update the player location. It then draws the images of passive tiles, and runs all transitions.

## Rooms, tiles and transitions

Each room in Paganitzu is a 16x12 grid of tiles. A tile can either be passive (not moving or reacting) or active (moving around, showing an animated effect, or waiting for an event to happen). While playing the game, the tiles are updated: spiders move around, keys are collected, walls disappear, spiders are watching the player, etcetera. This is all implemented with "transitions". A transition updates an active tile based on the current game state. Examples are:

* RevealRoom: a pattern of blue images covers the screen, and gradually disappears to reveal the room. When this transition is finished, it triggers a Spawn transition.
* Spawn: displays a "puff of smoke" animation and then spawns the player in the starting position of the room.
* PlayerMove: the player moves around
* SpiderMove: a spider moves around the screen
* SnakeGuard: turn a snake to watch in the player's direction. If the player is directly in front of the snake, a SnakeBite transition is started.
* SnakeBite: a venomous bolt is fired from the snake to the player, and the player dies.
* DoorLocked: verifies if there are any keys left in the room. If not, the door is unlocked.
* GemSparkle: a randomly triggered transition that will draw a "sparkle" animation on a gem.

A number of images share a common `Animation` base class. An animation is simply a list of images that is displayed sequentially.

All transitions are waiting on a queue, and every gameplay iteration (10 times per second), they are processed. Most transitions will return a `CONTINUE` result, and are enqueued again. A transition that is completely finished will return `DONE` and not enqueued anymore. Some transitions (like the `WaterFlow` animation) will continue forever and never finish.

Many transitions are triggered by very specific events. Some are only applicable in certain rooms. This is hard-coded in the method `GameSession.scheduleTransitions()`.

### Drawing the screen

All graphics are read from the original Paganitzu shareware game data file `PAGA1.012`. This file contains a list of 100 pixel art graphics with 16-bit EGA colors (see description [here](https://moddingwiki.shikadi.net/wiki/Paganitzu_Graphics_Format)). Each picture is converted into a RGBA-formatted cairo `ImageSurface` object. This implemented in the `TileReader` class.

The room layout is read from the file `PAGA1.007` in the `LevelReader` class. The file contains the exact layout of the 20 rooms; every tile has a number that corresponds to its type (a wall, spider, empty space, key, ...). Many types of tile have multiple variants: for example, there are 4 types of spider, depending of the direction they are expected to move. The tiles are loaded into a `Room` object that represent the level in its initial state. The tiles have a state (passive or active) and an image to display.

The core gameplay loop in the `GameSession.updateState()` method iterates through the tiles and draws the images. It then runs all transitions; most transitions will also draw one or more image. The loop runs 10 times per second (10 FPS).

The actual drawing is done with the Cairo graphics library. Cairo uses a `Context` class that can execute drawing commands like drawing lines, displaying an image, transforming images, etcetera.  All drawing operations in the game are executed in `DrawCommand` actions that perform an operation on a Cairo `Context`.

### The user interface

The user interface is created with Gtk and LibAdwaita. The `GameApplication` class represents the base application, and will open a `GameWindow`. The `GameWindow` is a Gtk composite template class with the actual layout in the XML file `GameWindow.ui`. The `GameWindow` class contains most of the callback functions for user input (key presses, saving, loading and pausing the game, ...). The `GamePaintable` class is where the game graphics are displayed. It implements the Gtk `Paintable` interface and uses the Cairo graphics library to execute a list of `DrawCommand` actions.

### Saving and loading

To keep things simple, there is one "savegame slot". Saving the game is implemented using standard Java object serialization. The current `GameSession` instance is serialized with `ObjectOutputStream.writeObject()` into a `DeflaterOutputStream`, which then writes to a `FileOutputStream`. The reverse is done for loading a savegame. In this way, the entire saving and loading functionality was implemented in just a few lines of code.

### Downloading game assets

The Paganitzu shareware game is downloaded from archive.org and cached locally. The `ArchiveReader` class extracts the zip file into memory and reads the contents of the data files into byte arrays for further processing (see above).

## Not implemented

* Paganitzu had very simple PC speaker sounds, most likely generated with BASIC's `SOUND` statement. A similar effect can be obtained with the Audacity tone generator. (I did this for my [remake of Tunneler](https://github.com/jwharm/tunneler).) The resulting files can be included as `.ogg` resources and played with `GtkMediaStream` in the relevant transition classes.
* All textual prompts and story elements from Paganitzu were omitted. I don't know how to read these from the data files, and including a verbatim copy with the Java application is obviously not an option. Play the original game if you want to enjoy the game with the story.
* Paganitzu featured beautiful full-screen images in the story screens and hidden rooms. It shouldn't be too hard to read these images from the data files (presumably `PAGA1.010` and `PAGA1.016`) with a slightly modified version of the `TileReader` class. But without the accompanying text, most images aren't very useful.
* Episode 2 and 3 of the full Paganitzu game are not playable with "Tribute to Paganitzu".

## Extending the game

You can easily change the behavior of the game by modifying the source code, especially the transition classes. You can also create new levels as custom `Room` objects. Given enough creativity, it is also possible to create a brand new game using this engine.

## License

"Tribute to Paganitzu" is available to be redistributed and/or modified under the terms of the GNU General Public License (GPL) version 3, or (at your option) any later version. "Tribute to Paganitzu" is not affiliated with the game "Paganitzu" in any way, and as far as I understand, it is not allowed to distribute them together.

According to the in-game menu, "Paganitzu" is © 1991 by Trilobyte. It is published by 3DRealms. If you like this "Tribute to Paganitzu", I highly recommend buying the full game from 3DRealms.
