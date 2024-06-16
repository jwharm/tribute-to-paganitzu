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
import io.github.jwharm.tribute.ui.Messages;

import java.util.List;

import static io.github.jwharm.tribute.engine.Image.*;

/**
 * This animation is displayed when the player is impaled by spikes.
 * It is not exactly the same as in the original game but it's close enough.
 */
public class Impale extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(HIT_2);
    private static final boolean LOOP = false;

    public Impale(Tile player) {
        super(DELAY, player, IMAGES, LOOP);
    }

    @Override
    public Result run(GameSession game) {
        // Run the animation
        var result = super.run(game);

        // Reset the game when the animation is done
        if (result == Result.DONE) {
            game.state().showMessage(Messages.PLAYER_DIED);
            game.schedule(new LoadRoom(LoadRoom.Action.RESET_ROOM));
        }

        return result;
    }
}
