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

/**
 * Make the little spider move around the room. When the spider encounters the
 * player, the {@link Die} transition is triggered.
 */
public class SpiderMove implements Transition {

    private final Tile spider;
    private float progress = 0;

    public SpiderMove(Tile spider) {
        this.spider = spider;
    }

    /*
     * Calculate in which direction the spider should move next. The result is
     * stored in the {@code spider.direction} field. When the spider cannot
     * move, the direction is set to {@code null}.
     */
    private void updateDirection(Room room) {
        Position position = new Position(spider.row(), spider.col());
        boolean clockwise = spider.id() == 38 || spider.id() == 40;

        if (spider.direction() == null)
            return;

        // Look in all four directions for a valid move
        Direction initial = spider.direction().next(clockwise), next = initial;
        do {
            Tile tile = room.get(position.move(next));
            if (tile.type() == ActorType.EMPTY) {
                // Empty space found: move in this direction
                spider.setDirection(next);
                return;
            }
            next = next.next(!clockwise);
        } while (next != initial);

        // Nowhere to go
        spider.setDirection(null);
    }

    @Override
    public Result run(GameSession game) {
        if (game.frozen())
            return Result.CONTINUE;

        progress += 0.5f;
        Position current = new Position(spider.row(), spider.col());

        if (progress < 1) {
            updateDirection(game.room());
            if (spider.direction() == null) {
                explode(game);
                return Result.DONE;
            }

            game.draw(current, Image.EMPTY);
            game.draw(current.move(spider.direction(), progress), Image.SPIDER_1);
            game.room().swap(
                    spider,
                    game.room().get(spider.position().move(spider.direction()))
            );
            return Result.CONTINUE;
        } else {
            game.draw(current, Image.SPIDER_2);
            if (!bite(game))
                game.schedule(new SpiderMove(spider));
            return Result.DONE;
        }
    }

    // Check if the spider is near the player, and trigger the `Die` transition
    private boolean bite(GameSession game) {
        Tile player = game.room().player();
        if (player != null && spider.position().borders(player.position())) {
            game.freeze();
            game.state().die();
            game.schedule(new Die(player));
            return true;
        }
        return false;
    }

    // Remove the spider and spawn gems on and around its position
    private void explode(GameSession game) {
        game.schedule(new Spawn(spider, Tile.createGem()));
        // Spawn gems in all four directions: up, down, left and right
        for (var direction : Direction.values()) {
            Tile tile = game.room().get(spider.position().move(direction));
            if (turnIntoGem(tile))
                game.schedule(new Spawn(tile, Tile.createGem()));
        }
    }

    // Whether to spawn a gem on this tile when a spider explodes next to it
    private boolean turnIntoGem(Tile tile) {
        return switch (tile.type()) {
            case BOULDER, MUD -> true;
            // Only straight pipes, no turns
            case PIPE -> List.of(13, 14, 19, 20).contains((int) tile.id());
            default -> false;
        };
    }
}
