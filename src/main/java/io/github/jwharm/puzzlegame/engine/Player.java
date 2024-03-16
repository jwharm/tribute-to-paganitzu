package io.github.jwharm.puzzlegame.engine;

/**
 * Technically the player is a tile like any other, but we have to keep track
 * of the tile currently "below" it, so that when the player moves, it is
 * restored.
 */
public class Player extends Tile {

    private Tile current;

    public Player(short id, ActorType type, TileState state, Image image) {
        super(id, type, state, image);
    }

    public void setCurrent(Tile tile) {
        current = tile;
    }

    public Tile current() {
        return current;
    }
}
