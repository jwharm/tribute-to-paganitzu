package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

/**
 * This transition waits until the player is above the spike, and then triggers
 * the SpikeExtend transition.
 */
public class SpikeGuard implements Transition {

    private final Tile spike;

    public SpikeGuard(Tile spike) {
        this.spike = spike;
    }

    @Override
    public Result run(Game game) {
        Tile player = game.room().player();
        if (player == null)
            return Result.CONTINUE;

        // If the player is not right above the spike, continue
        if (player.col() != spike.col() || player.row() >= spike.row())
            return Result.CONTINUE;

        // If something blocks the spike, continue
        for (int row = player.row() + 1; row < spike.row(); row++)
            if (game.room().get(row, player.col()).type() != ActorType.EMPTY)
                return Result.CONTINUE;

        /*
         * The spike must immediately start moving up, otherwise it can be too
         * easily evaded. Therefore, we cannot wait until the transition is run
         * in the next frame, and trigger the first step from here. If there
         * are more steps, the transition is scheduled like usual.
         */
        Transition transition = new SpikeExtend(spike);
        if (transition.run(game) == Result.CONTINUE)
            game.schedule(transition);

        return Result.DONE;
    }
}
