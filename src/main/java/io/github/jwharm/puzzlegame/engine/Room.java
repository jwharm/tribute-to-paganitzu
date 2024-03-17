package io.github.jwharm.puzzlegame.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Paganitzu levels are called rooms. Each room is a grid of 12 rows and 16
 * columns. This class provides access to the tiles in a specific room.
 *
 */
public final class Room {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 12;

    private final Tile[][] tiles = new Tile[HEIGHT][WIDTH];
    public final EventQueue eventQueue = new EventQueue();

    /**
     * Return the tile on the requested coordinates.
     */
    public Tile get(int row, int col) {
        return row >= 0 && col >= 0 && row < HEIGHT && col < WIDTH
                ? tiles[row][col]
                : Tile.createOutOfBounds();
    }

    /**
     * Return the tile on the requested position.
     */
    public Tile get(Position pos) {
        return get((int) pos.row(), (int) pos.col());
    }

    /**
     * Return all tiles.
     */
    public List<Tile> getAll() {
        return Arrays.stream(tiles).flatMap(Arrays::stream).toList();
    }

    /**
     * Return all tiles with the requested type.
     */
    public List<Tile> getAll(ActorType type) {
        return Arrays.stream(tiles)
                .flatMap(Arrays::stream)
                .filter(t -> t.type() == type)
                .toList();
    }

    /**
     * Return one tile with the requested type.
     */
    public Tile getAny(ActorType type) {
        return Arrays.stream(tiles)
                .flatMap(Arrays::stream)
                .filter(t -> t.type() == type)
                .findAny()
                .orElse(null);
    }

    /**
     * Find the tile that the player is on.
     */
    public Tile player() {
        return getAny(ActorType.PLAYER);
    }

    public void set(int row, int col, Tile tile) {
        if (tile instanceof Player player)
            player.setCurrent(tiles[row][col]);

        tiles[row][col] = tile;
        tile.setPosition(row, col);
    }

    public void set(Position pos, Tile tile) {
        set((int) pos.row(), (int) pos.col(), tile);
    }

    public void swap(Tile tile1, Tile tile2) {
        int r = tile1.row(), c = tile1.col();
        set(tile2.row(), tile2.col(), tile1);
        set(r, c, tile2);
    }

    public void replace(Tile tile1, Tile tile2) {
        tile1.setState(TileState.REMOVED);
        set(tile1.row(), tile1.col(), tile2);
    }

    public void remove(Tile tile) {
        replace(tile, Tile.createEmpty());
    }

    /**
     * Debug logging
     */
    public void printToStdOut() {
        for (Tile[] row : tiles) {
            for (Tile tile : row)
                System.out.printf("%02d ", tile.id());
            System.out.println();
        }
    }
}
