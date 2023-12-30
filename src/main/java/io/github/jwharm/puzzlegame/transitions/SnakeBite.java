package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Tile;
import io.github.jwharm.puzzlegame.engine.Transition;

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
        game.draw(snake.row(), col, "0088");
        if (col != player.col()) return Result.CONTINUE;
        game.schedule(new Die());
        return Result.DONE;
    }
}
