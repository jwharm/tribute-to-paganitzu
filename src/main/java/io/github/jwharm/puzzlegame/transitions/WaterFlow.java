package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

public class WaterFlow extends Animation {

    private static final int DELAY = 8;
    private static final List<Image> IMAGES = List.of(WATER_5_1, WATER_5_2);
    private static final boolean LOOP = true;

    public WaterFlow(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
    }
}
