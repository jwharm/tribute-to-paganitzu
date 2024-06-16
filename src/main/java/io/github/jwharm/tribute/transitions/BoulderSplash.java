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
 * Push a boulder into a water tile. The boulder will disappear, and the water
 * will be replaced by an empty tile.
 */
public class BoulderSplash implements Transition {

    private final Tile boulder;
    private final Tile target;
    private final Direction direction;
    private float progress = 0;

    public BoulderSplash(Tile boulder, Tile target, Direction direction) {
        this.boulder = boulder;
        this.target = target;
        this.direction = direction;
        boulder.setState(TileState.ACTIVE);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result run(GameSession game) {
        progress += 0.5f;
        if (progress < 1) {
            game.room().remove(boulder);
            Position current = boulder.position().move(direction, progress);
            game.draw(current, Image.BOULDER);
            return Result.CONTINUE;
        } else {
            game.room().remove(target);
            game.draw(target.position(), Image.BOULDER_SPLASH);
            return Result.DONE;
        }
    }
}
