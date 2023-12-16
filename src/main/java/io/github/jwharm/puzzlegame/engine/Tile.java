package io.github.jwharm.puzzlegame.engine;

import java.util.HashMap;
import java.util.Map;

public class Tile {

    public static final Tile OUT_OF_BOUNDS = new Tile(ActorType.OUT_OF_BOUNDS, TileState.PASSIVE);

    private final ActorType type;
    private int row, col;
    private TileState state;
    private final Map<String, String> properties = new HashMap<>();

    public ActorType type() {
        return type;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public Position position() {
        return new Position(row, col);
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public TileState state() {
        return state;
    }

    public void draw(Game game) {
        game.draw(row, col, type().name() + ".png");
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setState(TileState state) {
        this.state = state;
    }

    public Tile(ActorType type, TileState state) {
        this.type = type;
        this.state = state;
    }
}
