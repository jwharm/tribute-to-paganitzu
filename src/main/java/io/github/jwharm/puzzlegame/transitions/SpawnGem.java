package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class SpawnGem implements Transition {

    private static final int DONE = 6, DELAY = 2;

    private final float row, col;
    private int progress = 0;

    public SpawnGem(Position position) {
        this.row = position.row();
        this.col = position.col();
    }

    @Override
    public Result run(Game game) {
        if (game.ticks() % DELAY != 0) return Result.CONTINUE;

        progress++;
        game.draw(row, col, "gem" + (progress % 2) + ".png");

        if (progress < DONE) return Result.CONTINUE;

        game.board().set((int) row, (int) col, new Tile(ActorType.GEM, TileState.PASSIVE));
        return Result.DONE;
    }
}
