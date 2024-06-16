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

package io.github.jwharm.puzzlegame.engine;

/**
 * This transition will wait until the requested type is in the designated spot,
 * to trigger a transition event.
 */
public class Trigger implements Transition {

    private final ActorType type;
    private final Position spot;
    private final Transition event;

    public Trigger(ActorType type, Position spot, Transition event) {
        this.type = type;
        this.spot = spot;
        this.event = event;
    }

    @Override
    public Result run(Game game) {
        if (game.room().get(spot).type() == type) {
            game.schedule(event);
            return Result.DONE;
        } else {
            return Result.CONTINUE;
        }
    }
}
