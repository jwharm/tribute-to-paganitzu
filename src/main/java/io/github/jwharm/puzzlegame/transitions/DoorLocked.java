package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.*;

/**
 * The DoorLocked transition checks if all keys have been collected, and then
 * triggers the DoorUnlocked transition.
 */
public class DoorLocked implements Transition {

    private final List<Tile> keys;
    private final Tile door;

    public DoorLocked(Tile door, List<Tile> keys) {
        this.door = door;
        this.keys = keys;
    }

    @Override
    public Result run(Game game) {
        // Check if all keys have been collected
        for (var key : keys)
            if (key.state() != TileState.REMOVED)
                return Result.CONTINUE;

        // Unlock the door
        Tile unlocked = new Tile(door.id(), ActorType.DOOR_UNLOCKED, TileState.ACTIVE, Image.LOCKED_DOOR);
        game.room().replace(door, unlocked);
        game.schedule(new DoorUnlocked(unlocked));
        return Result.DONE;
    }
}
