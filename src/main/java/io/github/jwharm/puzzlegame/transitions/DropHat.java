package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * An animation of a falling hat. It is triggered by the curse of baldness in
 * level 14.
 */
public class DropHat extends Animation {

    private static final int DELAY = 2;
    private static final List<Image> IMAGES = List.of(
            FALLING_HAT_1,
            FALLING_HAT_2,
            FALLING_HAT_3,
            FALLING_HAT_4);
    private static final boolean LOOP = false;
    private final Tile tile;

    public DropHat(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.tile = tile;
    }

    @Override
    public Result run(Game game) {
        Result result = super.run(game);
        if (result == Result.DONE) {
            Tile hat = new Tile((short) 0, ActorType.HAT, TileState.PASSIVE, FALLING_HAT_4);
            game.room().replace(tile, hat);
        }
        return result;
    }
}
