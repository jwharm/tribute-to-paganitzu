package io.github.jwharm.puzzlegame.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Board {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 12;

    private final Tile[][] tiles = new Tile[HEIGHT][WIDTH];

    public Tile get(int row, int col) {
        return row >= 0 && col >= 0 && row < HEIGHT && col < WIDTH ? tiles[row][col] : Tile.OUT_OF_BOUNDS;
    }

    public Tile get(Position pos) {
        return get((int) pos.row(), (int) pos.col());
    }

    public List<Tile> getAll() {
        return Arrays.stream(tiles).flatMap(Arrays::stream).toList();
    }

    public List<Tile> getAll(ActorType type) {
        return Arrays.stream(tiles).flatMap(Arrays::stream).filter(t -> t.type() == type).toList();
    }

    public Tile getAny(ActorType type) {
        return Arrays.stream(tiles).flatMap(Arrays::stream).filter(t -> t.type() == type).findAny().orElse(null);
    }

    public Tile player() {
        return Objects.requireNonNull(getAny(ActorType.PLAYER));
    }

    public void set(int row, int col, Tile tile) {
        tiles[row][col] = tile;
        tile.setRow(row);
        tile.setCol(col);
    }

    public void swap(Tile tile1, Tile tile2) {
        int r = tile1.row(), c = tile1.col();
        set(tile2.row(), tile2.col(), tile1);
        set(r, c, tile2);
    }

    public void replace(Tile tile1, Tile tile2) {
        set(tile1.row(), tile1.col(), tile2);
    }
}
