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
 * The SnakeBite transition will draw a venomous bolt that moves towards the
 * player. On impact, it will trigger the {@link Vaporize} transition. The game
 * is frozen during the SnakeBite transition: the attack cannot be evaded.
 */
public class SnakeBite implements Transition {

    private final Tile snake, player;
    private int col;

    public SnakeBite(Tile snake, Tile player) {
        this.snake = snake;
        this.player = player;
        this.col = snake.col();
    }

    @Override
    public Result run(Game game) {
        // Draw the venomous bolt in the next tile
        col += Integer.signum(player.col() - snake.col());
        game.draw(snake.row(), col, Image.VENOM);

        // Has the bolt reached the player?
        if (col != player.col())
            return Result.CONTINUE;

        // Start the Vaporize animation
        game.state().die();
        game.schedule(new Vaporize(player));
        return Result.DONE;
    }
}
