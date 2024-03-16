package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

/**
 * The SpikeExtend transition will "extend" the spikes up towards the player,
 * one tile per frame. When the player is reached, the {@link Impale}
 * transition is triggered. The gameplay is not frozen during this transition:
 * the spikes can be evaded if the player moves fast enough.
 */
public class SpikeExtend implements Transition {

    private final Tile spike;

    public SpikeExtend(Tile spike) {
        this.spike = spike;
    }

    @Override
    public Result run(Game game) {
        Tile target = game.room().get(spike.position().move(Direction.UP));
        Tile bars = new Tile(spike.id(), spike.type(), TileState.PASSIVE, Image.SPIKE_BARS);
        switch (target.type()) {
            case EMPTY -> {
                game.room().swap(spike, target);
                game.room().replace(target, bars);
                game.schedule(new SpikeExtend(spike));
            }
            case PLAYER -> {
                game.freeze();
                game.state().die();
                game.room().replace(spike, bars);
                game.schedule(new Impale(target));
            }
        }
        return Result.DONE;
    }
}
