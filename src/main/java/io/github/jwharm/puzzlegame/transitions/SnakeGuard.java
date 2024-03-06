package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class SnakeGuard implements Transition {

    private final Tile snake;
    private Direction direction = Direction.RIGHT; // When player starts exactly above/below snake, look right
    private int progress = 0;

    public SnakeGuard(Tile snake) {
        this.snake = snake;
    }

    @Override
    public Result run(Game game) {
        Tile player = game.board().player();

        direction = switch(Integer.compare(player.col(), snake.col())) {
            case -1 -> Direction.LEFT;
            case +1 -> Direction.RIGHT;
            default -> direction;
        };
        game.draw(snake.row(), snake.col(), nextImage(direction));

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

    private Image nextImage(Direction direction) {
        progress = (progress + 1) % 4;
        if (direction == Direction.LEFT) {
            if (progress == 0) return Image.SNAKE_LEFT_1;
            if (progress == 1) return Image.SNAKE_LEFT_2;
            if (progress == 2) return Image.SNAKE_LEFT_3;
            if (progress == 3) return Image.SNAKE_LEFT_4;
        }
        if (progress == 0) return Image.SNAKE_RIGHT_1;
        if (progress == 1) return Image.SNAKE_RIGHT_2;
        if (progress == 2) return Image.SNAKE_RIGHT_3;
        return Image.SNAKE_RIGHT_4;
    }
}
