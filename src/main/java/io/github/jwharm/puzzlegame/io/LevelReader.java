package io.github.jwharm.puzzlegame.io;

import io.github.jwharm.puzzlegame.engine.ActorType;
import io.github.jwharm.puzzlegame.engine.Board;
import io.github.jwharm.puzzlegame.engine.Tile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelReader {

    private static final Path TILE_DATA_FILE = Path.of("src", "main", "resources", "tile_names.txt");
    private static final Path LEVEL_PATH = Path.of("src", "main", "resources", "levels");

    private static Map<String, ActorType> loadTileData() throws IOException {
        Map<String, ActorType> tileData = new HashMap<>(160);
        for (String line : Files.readAllLines(TILE_DATA_FILE)) {
            String[] fields = line.split(" +");
            try {
                tileData.put(fields[0], ActorType.of(Integer.parseInt(fields[1])));
            } catch(NumberFormatException nfe) {
                throw new IOException(STR."Invalid number in \{TILE_DATA_FILE} line: \{line}");
            }
        }
        return tileData;
    }

    public static Board read(String levelFile) throws IOException {
        Map<String, ActorType> tileData = loadTileData();
        Board board = new Board();
        List<String> lines = Files.readAllLines(LEVEL_PATH.resolve(levelFile));
        if (lines.size() < Board.HEIGHT)
            throw new IOException(STR."Invalid layout in \{levelFile}:Expected \{Board.HEIGHT} rows, found \{lines.size()}");
        for (int row = 0; row < Board.HEIGHT; row++) {
            String[] columns = lines.get(row).split(" +");
            if (columns.length != Board.WIDTH)
                throw new IOException(STR."Invalid layout in \{levelFile} row \{row}: Expected \{Board.WIDTH} columns, found \{columns.length}");
            for (int col = 0; col < Board.WIDTH; col++) {
                ActorType actorType = tileData.get(columns[col]);
                board.set(row, col, new Tile(actorType, actorType.defaultTileState(), columns[col]));
            }
        }
        return board;
    }
}
