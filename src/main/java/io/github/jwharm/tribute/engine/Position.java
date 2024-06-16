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

import static java.lang.Math.abs;

/**
 * Represents the coordinates (row and column) of a tile in the room. The
 * coordinates are zero-indexed.
 */
public record Position(float row, float col) {

    public Position move(Direction direction) {
        return move(direction, 1);
    }

    /**
     * Return a position relative to this one.
     */
    public Position move(Direction direction, float distance) {
        return switch (direction) {
            case UP -> new Position(row - distance, col);
            case DOWN -> new Position(row + distance, col);
            case LEFT -> new Position(row, col - distance);
            case RIGHT -> new Position(row, col + distance);
        };
    }

    /**
     * Check if these two positions are in the same row and/or column and are
     * adjacent to each other.
     */
    public boolean borders(Position other) {
        return (this.col == other.col || this.row == other.row)
                && abs(this.col - other.col) <= 1
                && abs(this.row - other.row) <= 1;
    }
}
