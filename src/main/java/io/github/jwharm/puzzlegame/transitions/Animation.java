package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Animation implements Transition {

    private final int delay;
    private final Tile tile;
    private final Iterator<Image> iterator;
    private Image image;

    public Animation(int delay, Tile tile, List<Image> images, boolean loop) {
        this.delay = delay;
        this.tile = tile;
        this.tile.setState(TileState.ACTIVE);
        if (loop)
            // Infinitely looping iterator
            this.iterator = Stream.generate(() -> images).flatMap(Collection::stream).iterator();
        else
            // Regular iterator
            this.iterator = images.iterator();
    }

    @Override
    public Result run(Game game) {
        if (image == null || game.ticks() % delay == 0)
            image = iterator.next();
        game.draw(tile.row(), tile.col(), image);

        if (iterator.hasNext()) return Result.CONTINUE;

        tile.setState(TileState.PASSIVE);
        return Result.DONE;
    }
}
