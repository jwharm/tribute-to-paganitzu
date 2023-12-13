package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Tile;
import io.github.jwharm.puzzlegame.engine.Transition;

public class DoorUnlocked implements Transition {

    private static final int DELAY = 15;
    private final Tile door;
    private int state = 0;

    public DoorUnlocked(Tile door) {
        this.door = door;
    }

    @Override
    public Result run(Game game) {
        if (game.tick() % DELAY == 0)
            if (state == 0) state = 1; else state = 0;
        if (state == 1) game.draw(door.row(), door.col(), "unlocked.png");
        return Result.CONTINUE;
    }
}
