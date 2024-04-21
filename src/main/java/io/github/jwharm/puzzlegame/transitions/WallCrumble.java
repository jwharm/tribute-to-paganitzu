package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.ui.Messages;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * Some walls have hidden passages that will appear when the player pushes
 * against them. The WallCrumble transition will show a little animation,
 * remove the wall tile, and (depending on tile id) either expose a gem, or
 * display a message and reward the player with bonus points.
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
        var result = super.run(game);

        if (result == Result.DONE) {
            game.unfreeze();

            if (tile.id() == 5 || tile.id() == 6) {
                // Spawn gem
                game.room().replace(tile, Tile.createGem());
            } else {
                game.room().remove(tile);
                // Display message and assign bonus points (except in level 17)
                if (game.state().room() != 17) {
                    game.state().showMessage(Messages.HIDDEN_AREA);
                    game.state().addBonusReward();
                }
            }
        }

        return result;
    }
}
