package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

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
    public Result run(Game game) {
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
    private boolean bite(Game game) {
        Tile player = game.room().player();
        if (player != null && spider.position().borders(player.position())) {
            game.freeze();
            game.state().die();
            game.schedule(new Die(player));
            return true;
        }
        return false;
    }

    // Remove the spider from the room and spawn gems around its position
    private void explode(Game game) {
        game.room().set(
                spider.row(),
                spider.col(),
                new Tile((short) 0, ActorType.EMPTY, TileState.PASSIVE, Image.EMPTY)
        );
        // Spawn gems in all four directions: up, down, left and right
        for (var direction : Direction.values()) {
            Tile tile = game.room().get(spider.position().move(direction));
            Tile gem = new Tile((short) 10, ActorType.GEM, TileState.PASSIVE, Image.GEM_1);
            game.schedule(new Spawn(tile, gem));
        }
    }
}
