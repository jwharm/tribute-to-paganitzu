package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

// TODO: quick flash of the locked door (inverted colors)
public class DoorUnlocked implements Transition {

    private final float row, col;

    public DoorUnlocked(Position position) {
        this.row = position.row();
        this.col = position.col();
    }

    @Override
    public Result run(Game game) {
        return Result.DONE;
    }
}
