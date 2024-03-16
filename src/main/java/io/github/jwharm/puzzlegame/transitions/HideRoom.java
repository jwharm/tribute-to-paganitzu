package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * This class will draw blue squares on the screen, in a random pattern that is
 * increasingly dense, until the entire room is hidden beneath blue squares. It
 * is the reverse of the RevealRoom transition.
 */
public class HideRoom implements Transition {

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
    private int current = 0;

    public HideRoom() {
        list = new ArrayList<>(IntStream.range(0, BOARD_SIZE).boxed().toList());
        Collections.shuffle(list);
    }

    @Override
    public Result run(Game game) {
        /*
         * Increasingly draw more blue squares. The transition is complete
         * when all tiles are blue, meaning the `current` points to the last
         * one (== board size).
         */
        if (current == BOARD_SIZE)
            return Result.DONE;

        // Keep the player location hidden during the entire animation
        Tile player = game.room().player();
        if (player != null)
            game.draw(player.position(), Image.FLASH);

        /*
         * Draw the blue squares. They will appear in random spots because of
         * the random ordering of the list.
         */
        for (int i = 0; i < current; i++) {
            int t = list.get(i);
            int row = t / Room.WIDTH;
            int col = t % Room.WIDTH;
            game.draw(row, col, Image.FLASH);
        }

        current += STEP_SIZE;

        return Result.CONTINUE;
    }
}
