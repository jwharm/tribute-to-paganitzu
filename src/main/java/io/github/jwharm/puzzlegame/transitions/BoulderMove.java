package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class BoulderMove implements Transition {

    private final Tile boulder;
    private final Direction direction;
    private float progress = 0;

    public BoulderMove(Tile boulder, Direction direction) {
        this.boulder = boulder;
        this.direction = direction;
        boulder.setState(TileState.ACTIVE);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result run(Game game) {
        Tile target = game.board().get(boulder.position().move(direction));
        progress += 0.5f;
        Position current = new Position(boulder.row(), boulder.col());
        if (progress < 1) {
            game.draw(current.move(direction, progress), Image.BOULDER);
            game.board().swap(boulder, target);
            return Result.CONTINUE;
        } else {
            game.draw(current, Image.BOULDER);
            boulder.setState(TileState.PASSIVE);
            return Result.DONE;
        }
    }
}
