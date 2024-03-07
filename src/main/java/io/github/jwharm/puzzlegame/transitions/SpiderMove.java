package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class SpiderMove implements Transition {

    private final boolean clockwise;
    private final Direction direction;
    private final Tile spider;
    private float progress = 0;

    public SpiderMove(Tile spider, Direction direction) {
        this.spider = spider;
        this.clockwise = spider.direction() == Direction.LEFT; // default true
        this.direction = direction;
    }

    @Override
    public Result run(Game game) {
        if (game.paused())
            return Result.CONTINUE;

        progress += 0.5f;
        Position current = new Position(spider.row(), spider.col());
        if (progress < 1) {
            game.draw(current, Image.EMPTY);
            game.draw(current.move(direction, progress), Image.SPIDER_1);
            game.board().swap(
                    spider,
                    game.board().get(spider.position().move(direction))
            );
            return Result.CONTINUE;
        } else {
            game.draw(current, Image.SPIDER_2);
            if (!bite(game))
                scheduleNextMove(game);
            return Result.DONE;
        }
    }

    private boolean bite(Game game) {
        Tile player = game.board().player();
        if (spider.position().borders(player.position())) {
            game.schedule(new Die(player));
            return true;
        }
        return false;
    }

    private void scheduleNextMove(Game game) {
        Position position = new Position(spider.row(), spider.col());

        // Look in all four directions for a valid move
        Direction initial = direction.next(clockwise), next = initial;
        do {
            Tile tile = game.board().get(position.move(next));
            if (tile.type() == ActorType.EMPTY) {
                // Empty space found: move in this direction
                game.schedule(new SpiderMove(spider, next));
                return;
            }
            next = next.next(!clockwise);
        } while (next != initial);

        // Nowhere to go: explode
        explode(game);
    }

    private void explode(Game game) {
        game.board().set(
                spider.row(),
                spider.col(),
                new Tile(ActorType.EMPTY, TileState.PASSIVE, Image.EMPTY)
        );
        // Spawn gems in all four directions: up, down, left and right
        for (var direction : Direction.values()) {
            Tile tile = game.board().get(spider.position().move(direction));
            game.schedule(new SpawnGem(tile));
        }
    }
}
