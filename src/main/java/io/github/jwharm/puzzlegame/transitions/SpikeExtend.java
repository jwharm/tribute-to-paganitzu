package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

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
