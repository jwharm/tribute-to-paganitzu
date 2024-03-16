package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class BoulderSplash implements Transition {

    private final Tile boulder;
    private final Tile target;
    private final Direction direction;
    private float progress = 0;

    public BoulderSplash(Tile boulder, Tile target, Direction direction) {
        this.boulder = boulder;
        this.target = target;
        this.direction = direction;
        boulder.setState(TileState.ACTIVE);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result run(Game game) {
        progress += 0.5f;
        if (progress < 1) {
            game.room().remove(boulder);
            Position current = boulder.position().move(direction, progress);
            game.draw(current, Image.BOULDER);
            return Result.CONTINUE;
        } else {
            game.room().remove(target);
            game.draw(target.position(), Image.BOULDER_SPLASH);
            return Result.DONE;
        }
    }
}
