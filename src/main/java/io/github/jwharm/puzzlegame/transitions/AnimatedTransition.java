package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class AnimatedTransition implements Transition {

    private final int delay;
    private final Tile tile;
    private final Iterator<Image> iterator;

    public AnimatedTransition(int delay, Tile tile, List<Image> images, boolean loop) {
        this.delay = delay;
        this.tile = tile;
        if (loop)
            this.iterator = Stream.generate(() -> images).flatMap(Collection::stream).iterator();
        else
            this.iterator = images.iterator();
    }

    @Override
    public Result run(Game game) {
        if (game.ticks() % delay == 0) {
            Image image = iterator.next();
            game.draw(tile.row(), tile.col(), image);
            return iterator.hasNext() ? Result.CONTINUE : Result.DONE;
        }
        return Result.CONTINUE;
    }
}
