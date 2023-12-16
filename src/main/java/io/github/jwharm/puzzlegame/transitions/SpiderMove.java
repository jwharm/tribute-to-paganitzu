package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class SpiderMove implements Transition {

    private final boolean clockwise;
    private final Direction direction;
    private final Tile spider;
    private float progress = 0;

    public SpiderMove(Tile spider, Direction direction) {
        this.spider = spider;
        this.clockwise = !"false".equals(spider.getProperty("clockwise")); // default true
        this.direction = direction;
    }

    @Override
    public Result run(Game game) {
        if (game.paused()) return Result.CONTINUE;

        progress += 0.5f;
        Position current = new Position(spider.row(), spider.col());
        if (progress < 1) {
            game.draw(current.move(direction, progress), "spider.png");
            game.board().swap(spider, game.board().get(spider.position().move(direction)));
            return Result.CONTINUE;
        } else {
            game.draw(current, "spider.png");
            if (!bite(game))
                scheduleNextMove(game);
            return Result.DONE;
        }
    }

    private boolean bite(Game game) {
        if (spider.position().borders(game.board().player().position())) {
            game.schedule(new Die());
            return true;
        }
        return false;
    }

    private void scheduleNextMove(Game game) {
        Position current = new Position(spider.row(), spider.col());
        Direction next = direction.next(clockwise);
        do {
            if (game.board().get(current.move(next)).type() == ActorType.EMPTY) {
                game.schedule(new SpiderMove(spider, next));
                return;
            }
            next = next.next(!clockwise);
        } while (next != direction.next(clockwise));
        explode(game);
    }

    private void explode(Game game) {
        game.board().set(spider.row(), spider.col(), new Tile(ActorType.EMPTY, TileState.PASSIVE));
        game.schedule(new SpawnGem(spider.position().move(Direction.UP)));
        game.schedule(new SpawnGem(spider.position().move(Direction.DOWN)));
        game.schedule(new SpawnGem(spider.position().move(Direction.LEFT)));
        game.schedule(new SpawnGem(spider.position().move(Direction.RIGHT)));
    }
}
