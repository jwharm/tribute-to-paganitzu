/* Tribute to Paganitzu, a simple puzzle-game engine
 * Copyright (C) 2024 Jan-Willem Harmannij
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.jwharm.tribute.ui;

/**
 * This class contains text messages that are displayed in-game.
 */
public class Messages {

    public static final String DOWNLOAD_ERROR =
            "Error downloading file";

    public static final String INVALID_FILE =
            "The downloaded file cannot be used: It should be a zip file " +
            "that contains the Paganitzu shareware episode 1 installation " +
            "files.";

    public static final String PLAYER_DIED =
            "Press enter to continue";

    public static final String LEVEL_COMPLETED =
            """
            Congratulations!
            You earned %d bonus points.
            """;

    public static final String HIDDEN_AREA =
            "You found a hidden area!";

    public static final String LIGHT_SWITCH_FOUND =
            "Behind a crumbled wall, you found a light switch!";

    public static final String CURSED =
            """
            You have been cursed with baldness!
            
            What a silly nonsense.
            """;

    public static final String OOPS =
            "Oops.";
}
