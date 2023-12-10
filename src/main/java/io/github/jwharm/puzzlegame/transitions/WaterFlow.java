package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.Game;
import io.github.jwharm.puzzlegame.Result;
import io.github.jwharm.puzzlegame.Tile;
import io.github.jwharm.puzzlegame.Transition;

public class WaterFlow implements Transition {

    private final Tile tile;
    private int state = 0;

    public WaterFlow(Tile tile) {
        this.tile = tile;
    }

    @Override
    public int interval() {
        return 15;
    }

    @Override
    public Result update(Game game) {
        if (state == 0) state = 1; else state = 0;
        game.draw(tile.col(), tile.row(), "water" + state + ".png");
        return Result.CONTINUE;
    }
}
