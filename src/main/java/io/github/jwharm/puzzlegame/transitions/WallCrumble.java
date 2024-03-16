package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * Some walls have hidden passages that will appear when the player pushes
 * against them. The WallCrumble transition will show a little animation,
 * remove the wall tile, and (for certain wall types) expose a gem.
 */
public class WallCrumble extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(CRUMBLE_3, CRUMBLE_4);
    private static final boolean LOOP = false;

    private final Tile tile;

    public WallCrumble(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.tile = tile;
    }

    @Override
    public Result run(Game game) {
        // Run the animation
        game.freeze();
        var result = super.run(game);

        if (result == Result.DONE) {
            game.unfreeze();

            // Spawn new tile when the animation is done
            if (tile.id() == 5 || tile.id() == 6) {
                Tile replacement = new Tile((short) 10, ActorType.GEM, TileState.PASSIVE, Image.GEM_1);
                game.room().replace(tile, replacement);
            } else {
                game.room().remove(tile);
            }
        }

        return result;
    }
}
