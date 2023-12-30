package io.github.jwharm.puzzlegame.engine;

import java.util.NoSuchElementException;

public enum TileState {
    ACTIVE(0),
    PASSIVE(1);

    public final int id;

    TileState(int id) {
        this.id = id;
    }

    public static TileState of(int id) {
        for (TileState t : values()) if (t.id == id) return t;
        throw new NoSuchElementException();
    }
}
