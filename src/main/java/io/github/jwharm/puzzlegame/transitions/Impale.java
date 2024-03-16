package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Image;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Tile;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * This animation is displayed when the player is impaled by spikes.
 * It is not exactly the same as in the original game but it's close enough.
 */
public class Impale extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(HIT_2);
    private static final boolean LOOP = false;

    public Impale(Tile player) {
        super(DELAY, player, IMAGES, LOOP);
    }

    @Override
    public Result run(Game game) {
        // Run the animation
        var result = super.run(game);

        // Reset the game when the animation is done
        if (result == Result.DONE)
            game.schedule(new LoadRoom(false));

        return result;
    }
}
