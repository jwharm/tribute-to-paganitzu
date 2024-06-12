package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;
import org.freedesktop.cairo.Operator;

import static io.github.jwharm.puzzlegame.ui.GamePaintable.TILE_SIZE;

/**
 * The DoorUnlocked transition will alert the player that the door is open (all
 * keys have been collected).
 */
public class DoorUnlocked implements Transition {

    private final Tile door;
    private int status;

    public DoorUnlocked(Tile door) {
        this.door = door;
        this.status = 0;
    }

    @Override
    public Result run(Game game) {
        door.draw(game);

        if (status++ == 0)
            game.draw(cr -> {
                /*
                 * Draw the image with inverted colors. This will cause a short
                 * "flash" effect to signify that the door has been unlocked.
                 */
                cr.setSourceRGB(1, 1, 1)
                  .setOperator(Operator.DIFFERENCE)
                  .rectangle(
                          door.col() * TILE_SIZE,
                          door.row() * TILE_SIZE,
                          TILE_SIZE,
                          TILE_SIZE)
                  .clip()
                  .paint();
            });

        else if (game.state().room() != 1)
            return Result.DONE;

        // In room 1, draw a blinking arrow pointing to the unlocked door.
        int secondsPassed = status / 10;
        Position arrowLocation = door.position().move(Direction.UP);
        Position playerLocation = game.room().player().position();

        if (secondsPassed % 2 == 0 && !arrowLocation.equals(playerLocation))
            game.draw(door.position().move(Direction.UP), Image.ARROW);

        return Result.CONTINUE;
    }
}
