package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.ui.Messages;

/**
 * Applies the curse of baldness in level 14. First, the "cursed" status is set,
 * and a message is displayed. Then, after the player moved a step to the right,
 * his hat falls off and a second message is displayed.
 */
public class Baldness implements Transition {

    private static final Position CURSED_SPOT = new Position(1, 11);
    private DropHat dropHatAnimation;
    private boolean dropped = false;

    /**
     * This transition must run before PlayerMove, so the animation of the
     * falling hat does not draw on top of the player.
     */
    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result run(Game game) {
        Player player = game.room().player();

        // Apply curse & display message
        if (!player.cursed()) {
            player.curse();
            game.state().showMessage(Messages.CURSED);
        }

        // When moving player moves: Drop hat
        else if (!player.position().equals(CURSED_SPOT) && !dropped) {
            game.freeze();
            game.room().player().looseHat();
            if (dropHatAnimation == null)
                dropHatAnimation = new DropHat(game.room().get(CURSED_SPOT));

            Result result = dropHatAnimation.run(game);
            if (result == Result.DONE) {
                player.setDirection(Direction.LEFT);
                dropped = true;
            }
        }

        // After hat has dropped: Say "oops"
        else if (dropped) {
            game.state().showMessage(Messages.OOPS);
            game.unfreeze();
            return Result.DONE;
        }

        return Result.CONTINUE;
    }
}
