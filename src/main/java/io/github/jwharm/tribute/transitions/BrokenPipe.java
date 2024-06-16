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

/**
 * This will remove water tiles from level 15 when the pipe is broken.
 */
public class BrokenPipe implements Transition {

    @Override
    public Result run(GameSession game) {
        var room = game.room();

        var waterTile = new Tile((short) 35, ActorType.WATER, TileState.PASSIVE, Image.WATER_3_1);
        checkAndReplace(room, 5, 2, ActorType.WATER, waterTile);
        game.schedule(new WaterFlow(waterTile));

        checkAndReplace(room, 6, 2, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 7, 2, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 8, 2, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 2, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 3, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 4, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 5, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 6, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 7, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 8, ActorType.WATER, Tile.createEmpty());
        checkAndReplace(room, 9, 9, ActorType.WATER, Tile.createEmpty());

        var pipeTile = new Tile((short) 27, ActorType.PIPE, TileState.PASSIVE, Image.PIPE_END_4_DRY);
        checkAndReplace(room, 9, 10, ActorType.PIPE, pipeTile);

        return Result.DONE;
    }

    private void checkAndReplace(Room room, int row, int col,
                                 ActorType existingType, Tile newTile) {
        if (room.get(row, col).type() == existingType)
            room.replace(room.get(row, col), newTile);
    }
}
