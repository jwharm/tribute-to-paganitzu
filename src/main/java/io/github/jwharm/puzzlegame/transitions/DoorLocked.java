package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.*;

public class DoorLocked implements Transition {

    private final List<Position> keyLocations;
    private final Tile door;

    public DoorLocked(Tile door, List<Tile> keys) {
        this.door = door;
        keyLocations = keys.stream().map(k -> new Position(k.row(), k.col())).toList();
    }

    @Override
    public Result run(Game game) {
        // Check if all keys have been collected
        for (var position : keyLocations)
            if (game.board().get(position).type() == ActorType.KEY)
                return Result.CONTINUE;

        // Unlock the door
        Tile unlocked = new Tile(door.id(), ActorType.DOOR_UNLOCKED, TileState.ACTIVE, Image.LOCKED_DOOR);
        game.board().replace(door, unlocked);
        game.schedule(new DoorUnlocked(unlocked));
        return Result.DONE;
    }
}
