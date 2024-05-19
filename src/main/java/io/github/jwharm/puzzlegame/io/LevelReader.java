package io.github.jwharm.puzzlegame.io;

import io.github.jwharm.puzzlegame.engine.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * Helper class to read images a Paganitzu data file.
 * <p>
 * Credits to user <a href="https://moddingwiki.shikadi.net/wiki/User:K1n9_Duk3">K1n9_Duk3</a>
 * from the DOS Game Modding Wiki for documenting the
 * <a href="https://moddingwiki.shikadi.net/wiki/Paganitzu_Level_Format">Paganitzu data file format</a>.
 */
public class LevelReader {

    private static byte[] data;

    public static void setData(byte[] fileData) {
        data = fileData;
    }

    public static Room get(int id) {
        return readLevelsFile(data, id);
    }

    private static Room readLevelsFile(byte[] data, int id) {
        if (id < 1 || id > 20)
            throw new IllegalArgumentException("Invalid room number");

        Room room = new Room();

        // Skip the BASIC BSAVE/BLOAD header (7 bytes)
        int offset = 7;

        // The data consist of INT16LE values.
        ShortBuffer buffer = ByteBuffer.wrap(data, offset, data.length - offset)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();

        if (buffer.capacity() % 193 != 0)
            throw new IllegalArgumentException("Invalid levels file");

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 16; col++) {
                int index = id * 193 + row * 16 + col + 1;
                short t = buffer.get(index);
                ActorType type = toActorType(t);
                if (type == ActorType.PLAYER) {
                    // Empty tile "below" the player tile
                    room.set(row, col, Tile.createEmpty());
                    room.set(row, col, new Player(t, toActorType(t), TileState.PASSIVE, toImage(t)));
                } else {
                    room.set(row, col, new Tile(t, toActorType(t), TileState.PASSIVE, toImage(t)));
                }
            }
        }

        return room;
    }

    private static ActorType toActorType(short id) {
        return switch(id) {
            case 0, 45 -> ActorType.EMPTY;
            case 1, 2, 3, 4 -> ActorType.WALL;
            case 5, 6, 7, 8 -> ActorType.HIDDEN_PASSAGE;
            case 9 -> ActorType.KEY;
            case 10 -> ActorType.GEM;
            case 11, 12 -> ActorType.BOULDER;
            case 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28 -> ActorType.PIPE;
            case 29, 30, 31, 32, 33, 34, 35, 36, 37 -> ActorType.WATER;
            case 38, 39, 40, 41 -> ActorType.SPIDER;
            case 42 -> ActorType.DOOR_LOCKED;
            case 43 -> ActorType.SPIKES;
            case 44 -> ActorType.SNAKE;
            case 46 -> ActorType.MUD;
            case 47 -> ActorType.PLAYER;
            case 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82 -> ActorType.WARP;
            default -> throw new UnsupportedOperationException("Unknown id " + id);
        };
    }

    private static Image toImage(short id) {
        return switch(id) {
            case 0, 45, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82 -> EMPTY;
            case 1, 5 -> WALL_1;
            case 2, 6 -> WALL_2;
            case 3, 7 -> WALL_3;
            case 4, 8 -> WALL_4;
            case 9 -> KEY;
            case 10 -> GEM_1;
            case 11 -> BOULDER;
            case 12 -> BOULDER_SPLASH;
            case 13 -> PIPE_WALL_HORIZONTAL;
            case 14 -> PIPE_WALL_VERTICAL;
            case 15 -> PIPE_WALL_7;
            case 16 -> PIPE_WALL_9;
            case 17 -> PIPE_WALL_1;
            case 18 -> PIPE_WALL_3;
            case 19 -> PIPE_HORIZONTAL;
            case 20 -> PIPE_VERTICAL;
            case 21 -> PIPE_7;
            case 22 -> PIPE_9;
            case 23 -> PIPE_1;
            case 24 -> PIPE_3;
            case 25 -> PIPE_END_4_1;
            case 26 -> PIPE_END_6_1;
            case 27 -> PIPE_END_4_DRY;
            case 28 -> PIPE_END_6_DRY;
            case 29 -> WATER_5_1;
            case 30 -> WATER_7_1;
            case 31 -> WATER_8_1;
            case 32 -> WATER_9_1;
            case 33 -> WATER_1_1;
            case 34 -> WATER_2_1;
            case 35 -> WATER_3_1;
            case 36 -> WATER_4_1;
            case 37 -> WATER_6_1;
            case 38, 39, 40, 41 -> SPIDER_1;
            case 42 -> LOCKED_DOOR;
            case 43 -> SPIKES;
            case 44 -> SNAKE_LEFT_1;
            case 46 -> MUD;
            case 47 -> PLAYER_RIGHT_STAND;
            default -> throw new UnsupportedOperationException("Unknown id " + id);
        };
    }
}
