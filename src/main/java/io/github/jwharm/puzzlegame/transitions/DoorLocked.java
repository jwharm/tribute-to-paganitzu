package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

import java.util.*;

public class DoorLocked implements Transition {

    private final List<Position> keyLocations;
    private final Tile door;

    public DoorLocked(Tile door, Game game) {
        this.door = door;
        var keys = game.board().getAll(ActorType.KEY);
        keyLocations = keys.stream().map(k -> new Position(k.row(), k.col())).toList();
    }

    @Override
    public Result run(Game game) {
        for (var position : keyLocations)
            if (game.board().get(position).type() == ActorType.KEY)
                return Result.CONTINUE;
        door.setProperty("unlocked", "true");
        game.schedule(new DoorUnlocked(door));
        return Result.DONE;
    }
}
