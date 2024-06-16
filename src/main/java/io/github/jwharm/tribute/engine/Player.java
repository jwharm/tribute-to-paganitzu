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

/**
 * Technically the player is a tile like any other, but we have to keep track
 * of the tile currently "below" it, so that when the player moves, it is
 * restored.
 * <p>
 * We also keep track of the "curse of baldness" that can happen in room 14.
 */
public final class Player extends Tile {

    private Tile current;
    private boolean cursed = false;
    private boolean bald = false;

    public Player(short id, ActorType type, TileState state, Image image) {
        super(id, type, state, image);
    }

    private void updateImage() {
        image = switch (direction()) {
            case LEFT -> bald ? Image.PLAYER_NO_HAT_LEFT_STAND : Image.PLAYER_LEFT_STAND;
            case RIGHT -> bald ? Image.PLAYER_NO_HAT_RIGHT_STAND : Image.PLAYER_RIGHT_STAND;
            default -> image;
        };
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        updateImage();
    }

    public void setCurrent(Tile tile) {
        current = tile;
    }

    public Tile current() {
        return current;
    }

    public boolean cursed() {
        return cursed;
    }

    public boolean bald() {
        return bald;
    }

    public void curse() {
        cursed = true;
    }

    public void looseHat() {
        bald = true;
        updateImage();
    }

    public void pickupHat() {
        bald = false;
        updateImage();
    }
}
