package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class SnakeGuard implements Transition {

    private final Tile snake;
    private int progress = 0;
    private Direction direction = Direction.RIGHT; // When player starts exactly above/below snake, look right

    public SnakeGuard(Tile snake) {
        this.snake = snake;
    }

    @Override
    public Result run(Game game) {
        Tile player = game.board().player();

        direction = switch(Integer.compare(player.col(), snake.col())) {
            case -1 -> Direction.LEFT;
            default -> direction;
            case +1 -> Direction.RIGHT;
        };
        game.draw(snake.row(), snake.col(), "0020");

        // If not on same row, or something blocks the view, continue
        if (player.row() != snake.row()) return Result.CONTINUE;
        for (int col = min(player.col(), snake.col()) + 1; col < max(player.col(), snake.col()); col++)
            if (game.board().get(player.row(), col).type() != ActorType.EMPTY) return Result.CONTINUE;

        // Bite
        game.pause();
        game.schedule(new SnakeBite(snake, player));
        snake.setState(TileState.PASSIVE);
        return Result.DONE;
    }
}
