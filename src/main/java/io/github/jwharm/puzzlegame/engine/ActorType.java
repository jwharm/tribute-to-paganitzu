package io.github.jwharm.puzzlegame.engine;

import java.util.NoSuchElementException;

public enum ActorType {
    OUT_OF_BOUNDS(0),
    BOULDER(1),
    EMPTY(2),
    DOOR_LOCKED(3),
    DOOR_UNLOCKED(4),
    GEM(5),
    KEY(6),
    MUD(7),
    PLAYER(8),
    SNAKE(9),
    SPIDER(10),
    WALL(11),
    WATER(12);

    private final int id;

    ActorType(int id) {
        this.id = id;
    }

    public static ActorType of(int id) {
        for (ActorType a : values()) if (a.id == id) return a;
        throw new NoSuchElementException();
    }

    public TileState defaultTileState() {
        return switch(this) {
            case SNAKE, SPIDER, WATER -> TileState.ACTIVE;
            default -> TileState.PASSIVE;
        };
    }
}
