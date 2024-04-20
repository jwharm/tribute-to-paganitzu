package io.github.jwharm.puzzlegame.engine;

import java.util.NoSuchElementException;

public enum ActorType {
    OUT_OF_BOUNDS(0),
    BOULDER(1),
    EMPTY(2),
    DOOR_LOCKED(3),
    DOOR_UNLOCKED(4),
    GEM(5),
    HIDDEN_PASSAGE(6),
    KEY(7),
    MUD(8),
    PIPE(9),
    PLAYER(10),
    SNAKE(11),
    SPIDER(12),
    SPIKES(13),
    WALL(14),
    WARP(15),
    WATER(16);

    private final int id;

    ActorType(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static ActorType of(int id) {
        for (ActorType a : values()) if (a.id == id) return a;
        throw new NoSuchElementException();
    }
}
