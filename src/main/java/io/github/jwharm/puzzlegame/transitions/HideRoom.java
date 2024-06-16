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

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * This class will draw blue squares on the screen, in a random pattern that is
 * increasingly dense, until the entire room is hidden beneath blue squares. It
 * is the reverse of the RevealRoom transition.
 */
public class HideRoom implements Transition {

    /*
     * `32` is a convenient step size. With 192 tiles on the board and two
     * iterations, the total animation will take 192 / 32 = 6 frames.
     */
    private static final int STEP_SIZE = 32;
    private static final int BOARD_SIZE = Room.HEIGHT * Room.WIDTH;

    /*
     * We pick the tiles from a shuffled range of integers. The `current`
     * variable contains the current number of blue squares drawn on-screen.
     */
    private final ArrayList<Integer> list;
    private int current = 0;

    public HideRoom() {
        list = new ArrayList<>(IntStream.range(0, BOARD_SIZE).boxed().toList());
        Collections.shuffle(list);
    }

    @Override
    public Result run(GameSession game) {
        /*
         * Increasingly draw more blue squares. The transition is complete
         * when all tiles are blue, meaning the `current` points to the last
         * one (== board size).
         */
        if (current == BOARD_SIZE)
            return Result.DONE;

        // Keep the player location hidden during the entire animation
        Tile player = game.room().player();
        if (player != null)
            game.draw(player.position(), Image.FLASH);

        /*
         * Draw the blue squares. They will appear in random spots because of
         * the random ordering of the list.
         */
        for (int i = 0; i < current; i++) {
            int t = list.get(i);
            int row = t / Room.WIDTH;
            int col = t % Room.WIDTH;
            game.draw(row, col, Image.FLASH);
        }

        current += STEP_SIZE;

        return Result.CONTINUE;
    }
}
