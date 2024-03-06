package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class WaterFlow implements Transition {

    private static final int DELAY = 2;
    private final Tile tile;
    private int state = 0;

    public WaterFlow(Tile tile) {
        this.tile = tile;
    }

    @Override
    public Result run(Game game) {
        if (game.ticks() % DELAY == 0)
            if (state == 0) state = 1; else state = 0;
        game.draw(tile.row(), tile.col(), state == 0 ? Image.WATER_5_1 : Image.WATER_5_2);
        return Result.CONTINUE;
    }
}
