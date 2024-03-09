package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

public class WaterFlow extends Animation {

    private static final int DELAY = 4;
    private static final boolean LOOP = true;

    public WaterFlow(Tile tile) {
        super(
            DELAY,
            tile,
            List.of(tile.image(), Image.of(tile.image().id() + 1)),
            LOOP
        );
    }
}
