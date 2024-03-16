package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class Warp implements Transition {

    private final Tile player, source, destination;
    private Animation spawn;

    public Warp(Player player, Room room) {
        this.player = player;
        this.source = player.current();
        this.destination = room.getAll(ActorType.WARP).stream()
                .filter(tile -> tile.id() == source.id())
                .findAny().orElseThrow();
        this.spawn = new Spawn(player, source);
    }

    @Override
    public Result run(Game game) {
        game.freeze();

        Result result = spawn.run(game);
        if (player.position().equals(source.position())) {
            if (result == Result.DONE) {
                player.setState(TileState.PASSIVE);
                spawn = new Spawn(destination, player);
                result = spawn.run(game);
            }
        }

        return result;
    }
}
