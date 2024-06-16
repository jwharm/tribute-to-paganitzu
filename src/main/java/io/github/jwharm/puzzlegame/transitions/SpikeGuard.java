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

/**
 * This transition waits until the player is above the spike, and then triggers
 * the SpikeExtend transition.
 */
public class SpikeGuard implements Transition {

    private final Tile spike;

    public SpikeGuard(Tile spike) {
        this.spike = spike;
    }

    @Override
    public Result run(GameSession game) {
        Tile player = game.room().player();
        if (player == null)
            return Result.CONTINUE;

        // If the player is not right above the spike, continue
        if (player.col() != spike.col() || player.row() >= spike.row())
            return Result.CONTINUE;

        // If something blocks the spike, continue
        for (int row = player.row() + 1; row < spike.row(); row++)
            if (game.room().get(row, player.col()).type() != ActorType.EMPTY)
                return Result.CONTINUE;

        /*
         * The spike must immediately start moving up, otherwise it can be too
         * easily evaded. Therefore, we cannot wait until the transition is run
         * in the next frame, and trigger the first step from here. If there
         * are more steps, the transition is scheduled like usual.
         */
        Transition transition = new SpikeExtend(spike);
        if (transition.run(game) == Result.CONTINUE)
            game.schedule(transition);

        return Result.DONE;
    }
}
