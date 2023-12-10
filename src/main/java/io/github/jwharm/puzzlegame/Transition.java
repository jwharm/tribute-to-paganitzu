package io.github.jwharm.puzzlegame;

public interface Transition {
    int interval();
    Result update(Game game);
}
