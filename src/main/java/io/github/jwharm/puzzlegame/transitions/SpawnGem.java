package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

public class SpawnGem extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(POOF_1, POOF_2, POOF_3, POOF_4, POOF_5, POOF_6);
    private static final boolean LOOP = false;

    private final Tile tile;

    public SpawnGem(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.tile = tile;
    }

    @Override
    public Result run(Game game) {
        Result result = super.run(game);

        if (result == Result.DONE)
            game.board().set(tile.row(), tile.row(), new Tile(ActorType.GEM, TileState.PASSIVE, Image.GEM_1));

        return result;
    }
}
