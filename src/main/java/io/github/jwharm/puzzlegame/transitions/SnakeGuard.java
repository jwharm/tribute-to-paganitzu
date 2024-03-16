package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

public class SnakeGuard implements Transition {

    private final Tile snake;
    private Direction direction = Direction.RIGHT; // When player starts exactly above/below snake, look right

    private final Animation animateLeft, animateRight;

    public SnakeGuard(Tile snake) {
        this.snake = snake;
        this.snake.setState(TileState.ACTIVE);
        this.animateLeft = new Animation(
                1,
                snake,
                List.of(SNAKE_LEFT_1, SNAKE_LEFT_2, SNAKE_LEFT_3, SNAKE_LEFT_4, SNAKE_LEFT_3, SNAKE_LEFT_2),
                true
        );
        this.animateRight = new Animation(
                1,
                snake,
                List.of(SNAKE_RIGHT_1, SNAKE_RIGHT_2, SNAKE_RIGHT_3, SNAKE_RIGHT_4, SNAKE_RIGHT_3, SNAKE_RIGHT_2),
                true
        );
    }

    @Override
    public Result run(Game game) {
        Tile player = game.room().player();
        if (player == null)
            return Result.CONTINUE;

        // Look left or right
        direction = switch(Integer.compare(player.col(), snake.col())) {
            case -1 -> Direction.LEFT;
            case +1 -> Direction.RIGHT;
            default -> direction;
        };

        // Draw next animation frame
        if (direction == Direction.LEFT)
            animateLeft.run(game);
        else
            animateRight.run(game);

        // If not on same row, continue
        if (player.row() != snake.row())
            return Result.CONTINUE;

        // If something blocks the view, continue
        int start = min(player.col(), snake.col()) + 1;
        int end = max(player.col(), snake.col());
        for (int col = start; col < end; col++)
            if (game.room().get(player.row(), col).type() != ActorType.EMPTY)
                return Result.CONTINUE;

        // Bite
        game.freeze();
        game.schedule(new SnakeBite(snake, player));
        snake.setState(TileState.PASSIVE);
        return Result.DONE;
    }
}
