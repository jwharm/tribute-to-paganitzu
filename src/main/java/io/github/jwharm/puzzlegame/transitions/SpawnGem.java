package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.*;

public class SpawnGem implements Transition {

    private static final int DONE = 6;

    private final int row, col;
    private int progress = 0;

    public SpawnGem(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int interval() {
        return 15;
    }

    @Override
    public Result update(Game game) {
        progress++;
        game.draw(col * Game.TILE_SIZE, row * Game.TILE_SIZE, "gem" + (progress % 2) + ".png");

        if (progress < DONE) return Result.CONTINUE;

        game.board().set(row, col, new Tile(ActorType.GEM, false));
        return Result.DONE;
    }
}
