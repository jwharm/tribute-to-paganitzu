package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Image;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Tile;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

public class Die extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(HIT_1, HIT_2, HIT_3, HIT_4, HIT_5, HIT_6, HIT_7);
    private static final boolean LOOP = false;

    private boolean init = true;

    public Die(Tile player) {
        super(DELAY, player, IMAGES, LOOP);
    }

    @Override
    public Result run(Game game) {
        if (init) {
            game.pause();
            game.state().die();
            init = false;
        }
        return super.run(game);
    }
}
