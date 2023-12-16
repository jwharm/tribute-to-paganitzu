package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Transition;

public class Die implements Transition {

    @Override
    public Result run(Game game) {
        game.pause();
        game.state().die();
        return Result.DONE;
    }
}
