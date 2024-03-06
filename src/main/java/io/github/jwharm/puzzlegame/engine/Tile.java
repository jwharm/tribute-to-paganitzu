package io.github.jwharm.puzzlegame.engine;

import io.github.jwharm.puzzlegame.io.ImageCache;

public class Tile {

    public static final Tile OUT_OF_BOUNDS = new Tile(ActorType.OUT_OF_BOUNDS, TileState.PASSIVE, Image.EMPTY);

    private final ActorType type;
    private final Image image;
    private int row, col;
    private TileState state;
    private Direction direction;

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

    public TileState state() {
        return state;
    }

    public Direction direction() {
        return direction;
    }

    public void draw(Game game) {
        game.draw(row, col, image);
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

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Tile(ActorType type, TileState state, Image image) {
        this.type = type;
        this.state = state;
        this.image = image;
        this.direction = Direction.RIGHT;
    }
}
