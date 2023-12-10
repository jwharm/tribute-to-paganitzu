package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.*;

public class SpiderMove implements Transition {

    private static final int DONE = 8;

    private final boolean clockwise;
    private final Direction direction;
    private final Tile spider;
    private int progress = 0;

    public SpiderMove(Tile spider, Direction direction) {
        this.spider = spider;
        this.clockwise = "true".equals(spider.properties.get("clockwise"));
        this.direction = direction;
    }

    @Override
    public int interval() {
        return 1;
    }

    @Override
    public Result update(Game game) {
        progress++;
        var current = new Point(spider.col() * Game.TILE_SIZE, spider.row() * Game.TILE_SIZE);
        game.draw(current.move(direction, progress), "spider.png");

        if (progress == DONE / 2) {
            Tile target = game.board().get(spider.point().move(direction, 1));
            game.board().swap(spider, target);
        }

        if (progress < DONE) return Result.CONTINUE;

        Direction next = getNext(game.board());
        if (next != null)
            game.schedule(new SpiderMove(spider, next));
        else
            explode(game);
        return Result.DONE;
    }

    private Direction getNext(Board board) {
        Point current = new Point(spider.col(), spider.row());
        Direction next = direction;
        do {
            if (board.get(current.move(next, 1)).type() == ActorType.EMPTY)
                return next;
            next = direction.next(clockwise);
        } while (next != direction);
        return null;
    }

    private void explode(Game game) {
        game.board().set(spider.row(), spider.col(), new Tile(ActorType.EMPTY, false));
        game.schedule(new SpawnGem(spider.row() - 1, spider.col()));
        game.schedule(new SpawnGem(spider.row() + 1, spider.col()));
        game.schedule(new SpawnGem(spider.row(), spider.col() - 1));
        game.schedule(new SpawnGem(spider.row(), spider.col() + 2));
    }
}
