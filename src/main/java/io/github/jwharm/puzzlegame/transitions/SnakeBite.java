package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

/**
 * The SnakeBite transition will draw a venomous bolt that moves towards the
 * player. On impact, it will trigger the {@link Vaporize} transition. The game
 * is frozen during the SnakeBite transition: the attack cannot be evaded.
 */
public class SnakeBite implements Transition {

    private final Tile snake, player;
    private int col;

    public SnakeBite(Tile snake, Tile player) {
        this.snake = snake;
        this.player = player;
        this.col = snake.col();
    }

    @Override
    public Result run(Game game) {
        // Draw the venomous bolt in the next tile
        col += Integer.signum(player.col() - snake.col());
        game.draw(snake.row(), col, Image.VENOM);

        // Has the bolt reached the player?
        if (col != player.col())
            return Result.CONTINUE;

        // Start the Vaporize animation
        game.state().die();
        game.schedule(new Vaporize(player));
        return Result.DONE;
    }
}
