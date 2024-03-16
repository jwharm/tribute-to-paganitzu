package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.Random;

import static io.github.jwharm.puzzlegame.engine.TileState.REMOVED;

/**
 * Paganitzu contains 2 images for every water tile. They are swapped randomly
 * to simulate flowing water.
 * <p>
 * The WaterFlow transition keeps two tiles with the corresponding images. The
 * tiles are randomly swapped every 4 game frames.
 */
public class WaterFlow implements Transition {

    private final static Random RAND = new Random();
    private final static int DELAY = 4;
    private final Tile tile1, tile2;
    private final Position position;

    public WaterFlow(Tile tile) {
        tile1 = tile;
        tile2 = new Tile(tile.id(), tile.type(), tile.state(), Image.of(tile.image().id() + 1));
        position = tile.position();
    }

    @Override
    public Result run(Game game) {
        if (tile1.state() == REMOVED || tile2.state() == REMOVED)
            return Result.DONE;

        if (game.ticks() % DELAY == 0)
            game.room().set(position, RAND.nextBoolean() ? tile1 : tile2);

        return Result.CONTINUE;
    }
}
