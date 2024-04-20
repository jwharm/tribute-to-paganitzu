package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.ui.Messages;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * This animation is displayed when the player it bitten by a spider.
 */
public class Die extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(HIT_1, HIT_2, HIT_3, HIT_4, HIT_5, HIT_6, HIT_7);
    private static final boolean LOOP = false;

    public Die(Tile player) {
        super(DELAY, player, IMAGES, LOOP);
    }

    @Override
    public Result run(Game game) {
        // Run the animation
        var result = super.run(game);

        // Reset the game when the animation is done
        if (result == Result.DONE) {
            game.state().showMessage(Messages.PLAYER_DIED);
            game.schedule(new LoadRoom(false));
        }

        return result;
    }
}
