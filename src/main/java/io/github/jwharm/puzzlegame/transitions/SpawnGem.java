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

        Image image;
        if (progress == 1) image = Image.POOF_1;
        if (progress == 2) image = Image.POOF_2;
        if (progress == 3) image = Image.POOF_3;
        if (progress == 4) image = Image.POOF_4;
        if (progress == 5) image = Image.POOF_5;
        else image = Image.POOF_6;

        game.draw(row, col, image);

        if (progress < DONE) return Result.CONTINUE;

        game.board().set((int) row, (int) col, new Tile(ActorType.GEM, TileState.PASSIVE, Image.GEM_1));
        return Result.DONE;
    }
}
