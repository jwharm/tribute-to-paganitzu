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

package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.Random;

import static io.github.jwharm.puzzlegame.engine.TileState.REMOVED;

/**
 * Paganitzu contains 2 images for every water tile. They are swapped randomly
 * to simulate flowing water.
 * <p>
 * The WaterFlow transition keeps two tiles with the corresponding images. The
 * tiles are randomly swapped every 4 game frames.
 */
public class WaterFlow implements Transition {

    private final static Random RAND = new Random();
    private final static int DELAY = 4;
    private final Tile tile1, tile2;
    private final Position position;

    public WaterFlow(Tile tile) {
        // When a saved game is loaded, restore the original image id.
        // The original id is always an odd number.
        int imageId = tile.image().id();
        if (imageId % 2 == 0) imageId--;

        tile1 = new Tile(tile.id(), tile.type(), tile.state(), Image.of(imageId));
        tile2 = new Tile(tile.id(), tile.type(), tile.state(), Image.of(imageId + 1));
        position = tile.position();
    }

    @Override
    public Result run(GameSession game) {
        if (tile1.state() == REMOVED || tile2.state() == REMOVED)
            return Result.DONE;

        if (game.ticks() % DELAY == 0)
            game.room().set(position, RAND.nextBoolean() ? tile1 : tile2);

        return Result.CONTINUE;
    }
}
