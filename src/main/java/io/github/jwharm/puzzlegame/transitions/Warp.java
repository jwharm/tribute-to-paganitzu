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

public class Warp implements Transition {

    private final Tile player, source, destination;
    private Animation spawn;

    public Warp(Player player, Room room) {
        this.player = player;
        this.source = player.current();
        this.destination = room.getAll(ActorType.WARP).stream()
                .filter(tile -> tile.id() == source.id())
                .findAny().orElseThrow();
        this.spawn = new Spawn(player, source);
    }

    @Override
    public Result run(GameSession game) {
        game.freeze();

        Result result = spawn.run(game);
        if (player.position().equals(source.position())) {
            if (result == Result.DONE) {
                player.setState(TileState.PASSIVE);
                spawn = new Spawn(destination, player);
                result = spawn.run(game);
            }
        }

        return result;
    }
}
