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

package io.github.jwharm.tribute.engine;

import java.util.NoSuchElementException;

/**
 * A tile is either passive, active (a transition is running), or removed.
 */
public enum TileState {
    ACTIVE(0),
    PASSIVE(1),
    REMOVED(2);

    public final int id;

    TileState(int id) {
        this.id = id;
    }

    public static TileState of(int id) {
        for (TileState t : values()) if (t.id == id) return t;
        throw new NoSuchElementException();
    }
}
