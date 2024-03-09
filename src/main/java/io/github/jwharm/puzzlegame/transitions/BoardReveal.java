package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * This class implements the effect that the entire game board is filled with
 * blue squares, in a random pattern that is increasingly dense. When the
 * entire board is filled with blue squares, the effect is reversed: each frame
 * will draw fewer squares, until the entire board is visible again.
 */
public class BoardReveal implements Transition {

    /*
     * There are two iterations: First we draw an increasing amount of squares,
     * then we draw a decreasing amount of squares.
     */
    private boolean first = true;

    /*
     * We pick the tiles from a shuffled range of integers. The `current`
     * variable contains the current number of blue squares drawn on-screen.
     */
    private final ArrayList<Integer> list;
    private final int size;
    private int current = 0;

    public BoardReveal() {
        size = Room.HEIGHT * Room.WIDTH;
        list = new ArrayList<>(IntStream.range(0, size).boxed().toList());
        Collections.shuffle(list);
    }

    @Override
    public Result run(Game game) {
        /*
         * In the first iteration (first=true) we increasingly draw more blue
         * squares, in the second iteration we draw fewer squares in each
         * frame. The first iteration is complete when all tiles are blue,
         * meaning the `current` points to the last one (== board size).
         */
        if (current == size)
            first = false;

        /*
         * When the second pass is complete (0 blue squares left on-screen),
         * resume the game and start the usual transitions.
         */
        if (!first && current == 0) {
            game.scheduleTransitions(game.board());
            game.unfreeze();
            return Result.DONE;
        }

        /*
         * Draw the blue squares. They will (dis)appear in random spots because
         * of the random ordering of the list.
         */
        for (int i = 0; i < current; i++) {
            int t = list.get(i);
            Tile tile = game.board().get(t / Room.WIDTH, t % Room.WIDTH);
            game.draw(tile.position(), Image.FLASH);
        }

        /*
         * In the first pass, we draw an increasing amount of blue squares in
         * each frame. In the second pass, we draw a decreasing amount.
         *
         * `32` is a convenient step size. With 192 tiles on the board and two
         * iterations, the total animation will take 192 / 32 * 2 = 12 frames.
         */
        if (first)
            current += 32;
        else
            current -= 32;

        return Result.CONTINUE;
    }
}
