package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;
import org.freedesktop.cairo.Operator;

import static io.github.jwharm.puzzlegame.ui.GamePaintable.TILE_SIZE;

public class DoorUnlocked implements Transition {

    private final Tile door;

    public DoorUnlocked(Tile door) {
        this.door = door;
    }

    @Override
    public Result run(Game game) {
        door.draw(game);
        game.draw(cr -> {
            /*
             * Draw the image with inverted colors. This will cause a short
             * "flash" effect to signify that the door has been unlocked.
             */
            cr.setSourceRGB(1, 1, 1)
              .setOperator(Operator.DIFFERENCE)
              .rectangle(door.col() * TILE_SIZE, door.row() * TILE_SIZE, TILE_SIZE, TILE_SIZE)
              .clip()
              .paint();
        });
        return Result.DONE;
    }
}
