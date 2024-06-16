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

import java.io.Serializable;

public sealed class Tile implements Serializable permits Player {

    public static Tile createOutOfBounds() {
        return new Tile((short) 0, ActorType.OUT_OF_BOUNDS, TileState.PASSIVE, Image.EMPTY);
    }

    public static Tile createEmpty() {
        return new Tile((short) 0, ActorType.EMPTY, TileState.PASSIVE, Image.EMPTY);
    }

    public static Tile createGem() {
        return new Tile((short) 10, ActorType.GEM, TileState.PASSIVE, Image.GEM_1);
    }

    private final short id;
    private final ActorType type;
    protected Image image;
    private int row, col;
    private TileState state;
    private Direction direction;

    public Tile(short id, ActorType type, TileState state, Image image) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.image = image;
        this.direction = id == 40 || id == 41 ? Direction.LEFT : Direction.RIGHT;
    }

    public short id() {
        return id;
    }

    public ActorType type() {
        return type;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public Position position() {
        return new Position(row, col);
    }

    public TileState state() {
        return state;
    }

    public Direction direction() {
        return direction;
    }

    public Image image() {
        return image;
    }

    public void draw(Game game) {
        game.draw(row, col, image);
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setState(TileState state) {
        this.state = state;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Tile %d,%d type=%s id=%d state=%s image=%s direction=%s"
                .formatted(row, col, type, id, state, image, direction);
    }
}
