package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Image;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Tile;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * This animation is displayed when the player gets hit by snake venom.
 */
public class Vaporize extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(
            VENOM_HIT_1, VENOM_HIT_2,
            CRUMBLE_1, CRUMBLE_1, CRUMBLE_1, CRUMBLE_1,
            CRUMBLE_2, CRUMBLE_3, CRUMBLE_4, CRUMBLE_5
    );
    private static final boolean LOOP = false;

    public Vaporize(Tile player) {
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
