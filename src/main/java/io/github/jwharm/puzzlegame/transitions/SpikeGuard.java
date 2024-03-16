package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

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

        // Extend the spike
        game.schedule(new SpikeExtend(spike));
        return Result.DONE;
    }
}
