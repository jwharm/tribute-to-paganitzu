package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.Game;
import io.github.jwharm.puzzlegame.Result;
import io.github.jwharm.puzzlegame.Tile;
import io.github.jwharm.puzzlegame.Transition;

public class Unlocked implements Transition {

    private final Tile door;
    private int state = 0;

    public Unlocked(Tile door) {
        this.door = door;
    }

    @Override
    public int interval() {
        return 15;
    }

    @Override
    public Result update(Game game) {
        if (state == 0) state = 1; else state = 0;
        if (state == 1) game.draw(door.col(), door.row(), "unlocked.png");
        return Result.CONTINUE;
    }
}
