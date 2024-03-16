package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

public class Spawn extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(POOF_1, POOF_2, POOF_3, POOF_4, POOF_5, POOF_6);
    private static final boolean LOOP = false;

    private final Tile originalTile, newTile;

    public Spawn(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.originalTile = tile;
        this.newTile = null;
    }

    public Spawn(Tile originaltile, Tile newTile) {
        super(DELAY, originaltile, IMAGES, LOOP);
        this.originalTile = originaltile;
        this.newTile = newTile;
    }

    @Override
    public Result run(Game game) {
        Result result = super.run(game);

        if (result == Result.DONE) {
            if (newTile != null)
                game.room().replace(originalTile, newTile);
            game.unfreeze();
        }

        return result;
    }
}
