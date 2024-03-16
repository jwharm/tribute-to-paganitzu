package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Turn the snake in the player's direction, play the animation, and when the
 * player is right in front, spit venom (the {@link SnakeBite} animation).
 */
public class SnakeGuard implements Transition {

    private final static int DELAY = 1;
    private final static boolean LOOP = true;

    private final Tile snake;
    private final Animation animateLeft, animateRight;

    public SnakeGuard(Tile snake) {
        this.snake = snake;
        this.snake.setState(TileState.ACTIVE);
        this.animateLeft = new Animation(
                DELAY,
                snake,
                List.of(SNAKE_LEFT_1,
                        SNAKE_LEFT_2,
                        SNAKE_LEFT_3,
                        SNAKE_LEFT_4,
                        SNAKE_LEFT_3,
                        SNAKE_LEFT_2),
                LOOP);
        this.animateRight = new Animation(
                DELAY,
                snake,
                List.of(SNAKE_RIGHT_1,
                        SNAKE_RIGHT_2,
                        SNAKE_RIGHT_3,
                        SNAKE_RIGHT_4,
                        SNAKE_RIGHT_3,
                        SNAKE_RIGHT_2),
                LOOP);
    }

    @Override
    public Result run(Game game) {
        Tile player = game.room().player();
        if (player == null)
            return Result.CONTINUE;

        // Look left or right
        snake.setDirection(switch(Integer.compare(player.col(), snake.col())) {
            case -1 -> Direction.LEFT;
            case +1 -> Direction.RIGHT;
            default -> snake.direction();
        });

        // Draw next animation frame
        if (snake.direction() == Direction.LEFT)
            animateLeft.run(game);
        else
            animateRight.run(game);

        if (game.frozen())
            return Result.CONTINUE;

        // If not on same row, continue
        if (player.row() != snake.row())
            return Result.CONTINUE;

        // If something blocks the view, continue
        int start = min(player.col(), snake.col()) + 1;
        int end = max(player.col(), snake.col());
        for (int col = start; col < end; col++)
            if (game.room().get(player.row(), col).type() != ActorType.EMPTY)
                return Result.CONTINUE;

        // Bite (shoot a venomous bolt towards the player)
        game.freeze();
        game.schedule(new SnakeBite(snake, player));
        return Result.CONTINUE;
    }
}
