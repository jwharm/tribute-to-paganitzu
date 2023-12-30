package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class DoorUnlocked implements Transition {

    private static final int DONE = 5, DELAY = 2;

    private final float row, col;
    private int progress = 0;

    public DoorUnlocked(Position position) {
        this.row = position.row();
        this.col = position.col();
    }

    @Override
    public Result run(Game game) {
        game.draw(row, col, progress % 2 == 0 ? "0037": "0039");
        if (game.ticks() % DELAY != 0) return Result.CONTINUE;
        if (++progress < DONE) return Result.CONTINUE;
        game.board().set((int) row, (int) col, new Tile(ActorType.DOOR_UNLOCKED, TileState.PASSIVE, "0037"));
        return Result.DONE;
    }
}
