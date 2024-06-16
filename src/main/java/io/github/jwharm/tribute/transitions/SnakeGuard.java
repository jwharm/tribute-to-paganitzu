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

package io.github.jwharm.tribute.transitions;

import io.github.jwharm.tribute.engine.*;

import java.util.List;

import static io.github.jwharm.tribute.engine.Image.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Turn the snake in the player's direction, play the animation, and when the
 * player is right in front, spit venom (the {@link SnakeBite} animation).
 */
public class SnakeGuard implements Transition {

    private final static int DELAY = 1;
    private final static boolean LOOP = true;

    private final Tile snake;
    private final Animation animateLeft, animateRight;

    public SnakeGuard(Tile snake) {
        this.snake = snake;
        this.snake.setState(TileState.ACTIVE);
        this.animateLeft = new Animation(
                DELAY,
                snake,
                List.of(SNAKE_LEFT_1,
                        SNAKE_LEFT_2,
                        SNAKE_LEFT_3,
                        SNAKE_LEFT_4,
                        SNAKE_LEFT_3,
                        SNAKE_LEFT_2),
                LOOP);
        this.animateRight = new Animation(
                DELAY,
                snake,
                List.of(SNAKE_RIGHT_1,
                        SNAKE_RIGHT_2,
                        SNAKE_RIGHT_3,
                        SNAKE_RIGHT_4,
                        SNAKE_RIGHT_3,
                        SNAKE_RIGHT_2),
                LOOP);
    }

    @Override
    public Result run(GameSession game) {
        Tile player = game.room().player();
        if (player == null)
            return Result.CONTINUE;

        // Look left or right
        snake.setDirection(switch(Integer.compare(player.col(), snake.col())) {
            case -1 -> Direction.LEFT;
            case +1 -> Direction.RIGHT;
            default -> snake.direction();
        });

        // Draw next animation frame
        if (snake.direction() == Direction.LEFT)
            animateLeft.run(game);
        else
            animateRight.run(game);

        if (game.frozen())
            return Result.CONTINUE;

        // If not on same row, continue
        if (player.row() != snake.row())
            return Result.CONTINUE;

        // If something blocks the view, continue
        int start = min(player.col(), snake.col()) + 1;
        int end = max(player.col(), snake.col());
        for (int col = start; col < end; col++)
            if (game.room().get(player.row(), col).type() != ActorType.EMPTY)
                return Result.CONTINUE;

        // Bite (shoot a venomous bolt towards the player)
        game.freeze();
        game.schedule(new SnakeBite(snake, player));
        return Result.CONTINUE;
    }
}
