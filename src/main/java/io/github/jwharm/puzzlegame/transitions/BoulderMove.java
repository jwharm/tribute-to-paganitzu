package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

/**
 * The BoulderMove transition will move a boulder (large round stone) one
 * position in the specified direction.
 */
public class BoulderMove implements Transition {

    private final Tile boulder;
    private final Tile target;
    private final Direction direction;
    private float progress = 0;

    public BoulderMove(Tile boulder, Tile target, Direction direction) {
        this.boulder = boulder;
        this.target = target;
        this.direction = direction;
        boulder.setState(TileState.ACTIVE);
    }

    /**
     * This transition has higher priority compared to other transitions. This
     * is necessary when trying to trap a moving spider.
     */
    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result run(Game game) {
        progress += 0.5f;
        Position current = new Position(boulder.row(), boulder.col());
        if (progress < 1) {
            game.draw(current.move(direction, progress), Image.BOULDER);
            game.room().swap(boulder, target);
            return Result.CONTINUE;
        } else {
            game.draw(current, Image.BOULDER);
            boulder.setState(TileState.PASSIVE);
            return Result.DONE;
        }
    }
}
