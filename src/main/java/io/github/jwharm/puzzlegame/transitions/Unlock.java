package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.*;

import java.util.*;

public class Unlock implements Transition {

    private final List<Point> keyLocations;
    private final Tile door;

    public Unlock(Tile door, Game game) {
        this.door = door;
        var keys = game.board().getAll(ActorType.KEY);
        keyLocations = keys.stream().map(k -> new Point(k.col(), k.row())).toList();
    }

    @Override
    public int interval() {
        return 30;
    }

    @Override
    public Result update(Game game) {
        for (Point point : keyLocations)
            if (game.board().get(point).type() == ActorType.KEY)
                return Result.CONTINUE;
        door.properties.put("unlocked", "true");
        game.schedule(new Unlocked(door));
        return Result.DONE;
    }
}
