package io.github.jwharm.puzzlegame.engine;

public enum Direction {
    // in clockwise order:
    LEFT,
    UP,
    RIGHT,
    DOWN;

    public Direction next(boolean clockwise) {
        return clockwise
                ? values()[ordinal() == 3 ? 0 : ordinal() + 1]
                : values()[ordinal() == 0 ? 3 : ordinal() - 1];
    }
}
