package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * This class implements the effect that the entire game board starts hidden
 * beneath blue squares, with each frame drawing fewer squares, until the
 * entire room is revealed. It is the reverse of the HideRoom transition.
 */
public class RevealRoom implements Transition {

    /*
     * `32` is a convenient step size. With 192 tiles on the board and two
     * iterations, the total animation will take 192 / 32 = 6 frames.
     */
    private static final int STEP_SIZE = 32;
    private static final int BOARD_SIZE = Room.HEIGHT * Room.WIDTH;

    /*
     * We pick the tiles from a shuffled range of integers. The `current`
     * variable contains the current number of blue squares drawn on-screen.
     */
    private final ArrayList<Integer> list;
    private int current = BOARD_SIZE;

    public RevealRoom() {
        list = new ArrayList<>(IntStream.range(0, BOARD_SIZE).boxed().toList());
        Collections.shuffle(list);
    }

    @Override
    public Result run(Game game) {
        // Load the room to its initial state before the transition starts.
        if (current == BOARD_SIZE)
            game.load();

        /*
         * When the transition is complete (0 blue squares left on-screen),
         * resume the game and start the usual transitions.
         */
        if (current == 0)
            return Result.DONE;

        // Keep the player location hidden during the entire animation
        game.draw(game.room().player().position(), Image.FLASH);
        
        /*
         * Draw the blue squares. They will (dis)appear in random spots because
         * of the random ordering of the list.
         */
        for (int i = 0; i < current; i++) {
            int t = list.get(i);
            int row = t / Room.WIDTH;
            int col = t % Room.WIDTH;
            game.draw(row, col, Image.FLASH);
        }

        current -= STEP_SIZE;

        return Result.CONTINUE;
    }
}
