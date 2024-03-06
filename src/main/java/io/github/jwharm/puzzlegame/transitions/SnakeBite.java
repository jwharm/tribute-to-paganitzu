package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

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
        col += Integer.signum(player.col() - snake.col());
        game.draw(snake.row(), col, Image.VENOM);
        if (col != player.col()) return Result.CONTINUE;
        game.schedule(new Die());
        return Result.DONE;
    }
}
