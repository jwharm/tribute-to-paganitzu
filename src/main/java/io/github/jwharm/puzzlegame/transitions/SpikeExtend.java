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
 * The SpikeExtend transition will "extend" the spikes up towards the player,
 * one tile per frame. When the player is reached, the {@link Impale}
 * transition is triggered. The gameplay is not frozen during this transition:
 * the spikes can be evaded if the player moves fast enough.
 */
public class SpikeExtend implements Transition {

    private final Tile spike;

    public SpikeExtend(Tile spike) {
        this.spike = spike;
    }

    @Override
    public Result run(Game game) {
        Tile target = game.room().get(spike.position().move(Direction.UP));
        Tile bars = new Tile(spike.id(), spike.type(), TileState.PASSIVE, Image.SPIKE_BARS);
        switch (target.type()) {
            case EMPTY -> {
                game.room().swap(spike, target);
                game.room().replace(target, bars);
                game.schedule(new SpikeExtend(spike));
            }
            case PLAYER -> {
                game.freeze();
                game.state().die();
                game.room().replace(spike, bars);
                game.schedule(new Impale(target));
            }
        }
        return Result.DONE;
    }
}
