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

import java.util.Random;

/**
 * This transition moves the player to an adjacent tile.
 */
public class PlayerMove implements Transition {

    private final Position original;
    private final Player player;
    private final Direction direction;
    private float progress = 0;

    public PlayerMove(Tile player, Direction direction) {
        this.original = player.position();
        this.player = (Player) player;
        this.direction = direction;
        player.setState(TileState.ACTIVE);
    }

    /**
     * Player movement will run before all other transitions, except the
     * BoulderMove transition (the stone is pushed ahead, before the player
     * moves into its place).
     */
    @Override
    public int priority() {
        return 2;
    }

    @Override
    public Result run(Game game) {
        progress += 0.5f;
        Position current = new Position(player.row(), player.col());
        if (progress < 1) {
            Tile target = game.room().get(player.position().move(direction));
            game.draw(current.move(direction, progress), moveImage());
            game.room().set(player.position(), player.current());
            game.room().set(target.position(), player);
            return Result.CONTINUE;
        } else {
            game.draw(current, standImage());
            player.setState(TileState.PASSIVE);

            // If the target is a warp tile, start a warp transition
            if (player.current().type() == ActorType.WARP)
                game.schedule(new Warp(player, game.room()));

            // Randomly drop hat when cursed
            else if (player.cursed() && !player.bald()) {
                if (new Random().nextInt(50) == 0) {
                    game.room().player().looseHat();
                    game.schedule(new DropHat(game.room().get(original)));
                }
            }

            return Result.DONE;
        }
    }

    private Image moveImage() {
        if (player.bald())
            return player.direction() == Direction.RIGHT
                    ? Image.PLAYER_NO_HAT_RIGHT_MOVE
                    : Image.PLAYER_NO_HAT_LEFT_MOVE;
        else
            return player.direction() == Direction.RIGHT
                    ? Image.PLAYER_RIGHT_MOVE
                    : Image.PLAYER_LEFT_MOVE;
    }

    private Image standImage() {
        if (player.bald())
            return player.direction() == Direction.RIGHT
                    ? Image.PLAYER_NO_HAT_RIGHT_STAND
                    : Image.PLAYER_NO_HAT_LEFT_STAND;
        else
            return player.direction() == Direction.RIGHT
                    ? Image.PLAYER_RIGHT_STAND
                    : Image.PLAYER_LEFT_STAND;
    }
}
