package io.github.jwharm.puzzlegame.engine;

import java.util.ArrayList;
import java.util.List;

public final class Board {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 12;

    private final Tile[][] tiles = new Tile[HEIGHT][WIDTH];

    public Tile get(int row, int col) {
        return tiles[row][col];
    }

    public Tile get(Position pos) {
        return tiles[(int) pos.row()][(int) pos.col()];
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
                set(row, col, new Tile(ActorType.EMPTY, TileState.PASSIVE));
    }

    public Board(String definition) {
        var layout = definition.lines().toList();
        for (int row = 0; row < layout.size(); row++) {
            for (int col = 0; col < layout.get(row).length(); col++) {
                var id = layout.get(row).charAt(col);
                set(row, col, switch(id) {
                    case ' ' -> new Tile(ActorType.EMPTY, TileState.PASSIVE);
                    case '=' -> new Tile(ActorType.WALL, TileState.PASSIVE);
                    case '~' -> new Tile(ActorType.WATER, TileState.ACTIVE);
                    case '*' -> new Tile(ActorType.SPIDER, TileState.ACTIVE);
                    case 'P' -> new Tile(ActorType.PLAYER, TileState.PASSIVE);
                    default -> throw new IllegalStateException("Unexpected value: " + id);
                });
            }
        }
    }

    public Game newGame() {
        return new Game(this);
    }
}
