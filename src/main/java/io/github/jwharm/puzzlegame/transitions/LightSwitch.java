package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Image;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Transition;
import io.github.jwharm.puzzlegame.ui.Messages;

import java.util.Set;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * Turns on the light in room 17.
 */
public class LightSwitch implements Transition {

    /*
     * First display message, then make light. Otherwise, the spiders are drawn
     * already while the message dialog is shown.
     */
    private boolean messageDisplayed = false;

    @Override
    public Result run(Game game) {
        if (!messageDisplayed) {
            game.state().showMessage(Messages.LIGHT_SWITCH_FOUND);
            messageDisplayed = true;
            return Result.CONTINUE;
        } else {
            game.state().makeLight();
            return Result.DONE;
        }
    }

    /**
     * Only this set of images is drawn in a dark level.
     */
    public static Set<Image> visibleInDark() {
        return Set.of(
                POOF_1, POOF_2, POOF_3, POOF_4, POOF_5, POOF_6,
                PLAYER_LEFT_MOVE, PLAYER_RIGHT_MOVE,
                PLAYER_LEFT_STAND, PLAYER_RIGHT_STAND,
                CRUMBLE_1, CRUMBLE_2, CRUMBLE_3, CRUMBLE_4, CRUMBLE_5,
                HIT_1, HIT_2, HIT_3, HIT_4, HIT_5, HIT_6, HIT_7
        );
    }
}
