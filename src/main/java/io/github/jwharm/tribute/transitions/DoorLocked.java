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

package io.github.jwharm.tribute.transitions;

import io.github.jwharm.tribute.engine.*;

import java.util.*;

/**
 * The DoorLocked transition checks if all keys have been collected, and then
 * triggers the DoorUnlocked transition.
 */
public class DoorLocked implements Transition {

    private final List<Tile> keys;
    private final Tile door;

    public DoorLocked(Tile door, List<Tile> keys) {
        this.door = door;
        this.keys = keys;
    }

    @Override
    public Result run(GameSession game) {
        // Check if all keys have been collected
        for (var key : keys)
            if (key.state() != TileState.REMOVED)
                return Result.CONTINUE;

        // Unlock the door
        Tile unlocked = new Tile(door.id(), ActorType.DOOR_UNLOCKED, TileState.ACTIVE, Image.LOCKED_DOOR);
        game.room().replace(door, unlocked);
        game.schedule(new DoorUnlocked(unlocked));
        return Result.DONE;
    }
}
