package io.github.jwharm.puzzlegame;

import java.util.ArrayList;
import java.util.List;

public final class Board {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 12;

    private final Tile[][] tiles = new Tile[HEIGHT][WIDTH];

    public Tile get(int row, int col) {
        return tiles[row][col];
    }

    public Tile get(Point point) {
        return tiles[point.y()][point.x()];
    }

    public List<Tile> getAll(ActorType type) {
        List<Tile> selection = new ArrayList<>();
        for (int row = 0; row < HEIGHT; row++)
            for (int col = 0; col < WIDTH; col++)
                if (tiles[row][col].type() == type) selection.add(tiles[row][col]);
        return selection;
    }

    public Tile getAny(ActorType type) {
        for (int row = 0; row < HEIGHT; row++)
            for (int col = 0; col < WIDTH; col++)
                if (tiles[row][col].type() == type) return tiles[row][col];
        return null;
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

    public Board() {
        for (int row = 0; row < HEIGHT; row++)
            for (int col = 0; col < WIDTH; col++)
                tiles[row][col] = new Tile(ActorType.EMPTY, false);
    }
}
