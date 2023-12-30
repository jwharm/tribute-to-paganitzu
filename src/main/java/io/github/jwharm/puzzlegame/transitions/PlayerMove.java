package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

public class PlayerMove implements Transition {

    private final Tile player;
    private final Direction direction;
    private float progress = 0;

    public PlayerMove(Tile player, Direction direction) {
        this.player = player;
        this.direction = direction;
        player.setState(TileState.ACTIVE);
    }

    @Override
    public Result run(Game game) {
        Tile target = game.board().get(player.position().move(direction));
        progress += 0.5f;
        Position current = new Position(player.row(), player.col());
        if (progress < 1) {
            game.draw(current, "0017");
            game.draw(current.move(direction, progress), "0004");
            game.board().swap(player, target);
            return Result.CONTINUE;
        } else {
            game.draw(current, "0004");
            player.setState(TileState.PASSIVE);
            return Result.DONE;
        }
    }
}
