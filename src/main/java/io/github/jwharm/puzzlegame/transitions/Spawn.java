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

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

public class Spawn extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(POOF_1, POOF_2, POOF_3, POOF_4, POOF_5, POOF_6);
    private static final boolean LOOP = false;

    private final Tile originalTile, newTile;

    public Spawn(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.originalTile = tile;
        this.newTile = null;
    }

    public Spawn(Tile originaltile, Tile newTile) {
        super(DELAY, originaltile, IMAGES, LOOP);
        this.originalTile = originaltile;
        this.newTile = newTile;
    }

    @Override
    public Result run(GameSession game) {
        Result result = super.run(game);

        if (result == Result.DONE) {
            if (newTile != null)
                game.room().replace(originalTile, newTile);
            game.unfreeze();
        }

        return result;
    }
}
