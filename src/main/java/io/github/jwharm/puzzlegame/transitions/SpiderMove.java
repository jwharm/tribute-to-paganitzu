package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class SpiderMove implements Transition {

    private final boolean clockwise;
    private final Direction direction;
    private final Tile spider, target;
    private float progress = 0;

    public SpiderMove(Tile spider, Tile target, Direction direction) {
        this.spider = spider;
        this.target = target;
        this.clockwise = !"false".equals(spider.getProperty("clockwise")); // default true
        this.direction = direction;
    }

    @Override
    public Result run(Game game) {
        game.draw(spider.position(), "empty.png");
        game.draw(target.position(), "empty.png");
        progress += 0.5f;
        Position current = new Position(spider.row(), spider.col());
        if (progress < 1) {
            game.draw(current.move(direction, progress), "spider.png");
            game.board().swap(spider, target);
            return Result.CONTINUE;
        } else {
            game.draw(current, "spider.png");
            scheduleNextMove(game);
            return Result.DONE;
        }
    }

    private void scheduleNextMove(Game game) {
        Position current = new Position(spider.row(), spider.col());
        Direction next = direction.next(clockwise);
        do {
            Tile target = game.board().get(current.move(next));
            if (target.type() == ActorType.EMPTY) {
                game.schedule(new SpiderMove(spider, target, next));
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
