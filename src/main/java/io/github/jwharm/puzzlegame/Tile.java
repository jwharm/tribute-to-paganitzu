package io.github.jwharm.puzzlegame;

import java.util.HashMap;
import java.util.Map;

public class Tile {

    private final ActorType type;
    private int row, col;
    private boolean transitioning = false;
    public final Map<String, String> properties = new HashMap<>();

    public ActorType type() {
        return type;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public Point point() {
        return new Point(row, col);
    }

    public boolean isTransitioning() {
        return transitioning;
    }

    public void draw(Game game) {
        game.draw(col * Game.TILE_SIZE, row * Game.TILE_SIZE, type().name() + ".png");
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setTransitioning(boolean transitioning) {
        this.transitioning = transitioning;
    }

    public Tile(ActorType type, boolean transitioning) {
        this.type = type;
        this.transitioning = transitioning;
    }
}
