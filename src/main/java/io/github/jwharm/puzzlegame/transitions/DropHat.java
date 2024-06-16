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

/**
 * An animation of a falling hat. It is triggered by the curse of baldness in
 * level 14.
 */
public class DropHat extends Animation {

    private static final int DELAY = 2;
    private static final List<Image> IMAGES = List.of(
            FALLING_HAT_1,
            FALLING_HAT_2,
            FALLING_HAT_3,
            FALLING_HAT_4);
    private static final boolean LOOP = false;
    private final Tile tile;

    public DropHat(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.tile = tile;
    }

    @Override
    public Result run(Game game) {
        Result result = super.run(game);
        if (result == Result.DONE) {
            Tile hat = new Tile((short) 0, ActorType.HAT, TileState.PASSIVE, FALLING_HAT_4);
            game.room().replace(tile, hat);
        }
        return result;
    }
}
