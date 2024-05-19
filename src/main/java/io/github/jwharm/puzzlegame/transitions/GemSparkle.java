package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Animation;
import io.github.jwharm.puzzlegame.engine.Image;
import io.github.jwharm.puzzlegame.engine.Tile;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * A sparkle animation on a gem.
 */
public class GemSparkle extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(GEM_1, GEM_2, GEM_3, GEM_4, GEM_5, GEM_6, GEM_7);
    private static final boolean LOOP = false;

    public GemSparkle(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
    }
}
