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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Given a list of images, this class will display them one by one sequentially
 * with a provided delay in between. If the {@code loop} argument is set, the
 * iterator will rewind to the first image and keep looping infinitely.
 * Otherwise, the animation will stop (return {@link Result#DONE}) after one
 * iteration.
 */
public class Animation implements Transition {

    private final int delay;
    private final Tile tile;
    private final Iterator<Image> iterator;
    private Image image;

    public Animation(int delay, Tile tile, List<Image> images, boolean loop) {
        this.delay = delay;
        this.tile = tile;
        this.tile.setState(TileState.ACTIVE);

        if (loop)
            // Infinitely looping iterator
            this.iterator = Stream.generate(() -> images)
                    .flatMap(Collection::stream)
                    .iterator();
        else
            // Regular iterator
            this.iterator = images.iterator();
    }

    @Override
    public Result run(GameSession game) {
        // Check if the tile has been removed
        if (tile.state() == TileState.REMOVED)
            return Result.DONE;

        // Draw the next animation frame
        if (image == null || game.ticks() % delay == 0)
            image = iterator.next();
        game.draw(tile.row(), tile.col(), image);

        if (iterator.hasNext())
            return Result.CONTINUE;

        tile.setState(TileState.PASSIVE);
        return Result.DONE;
    }
}
