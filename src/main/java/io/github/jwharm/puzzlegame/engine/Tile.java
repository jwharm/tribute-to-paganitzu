package io.github.jwharm.puzzlegame.engine;

import java.util.HashMap;
import java.util.Map;

public class Tile {

    public static final Tile OUT_OF_BOUNDS = new Tile(ActorType.OUT_OF_BOUNDS, TileState.PASSIVE, "0017");

    private final Map<String, String> properties = new HashMap<>();
    private final ActorType type;
    private final String tileRef;
    private int row, col;
    private TileState state;

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

    public String tileRef() {
        return tileRef;
    }

    public void draw(Game game) {
        game.draw(row, col, tileRef);
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

    public Tile(ActorType type, TileState state, String ref) {
        this.type = type;
        this.state = state;
        this.tileRef = ref;
    }
}
