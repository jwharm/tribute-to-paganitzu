package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

/**
 * This transition moves the player to an adjacent tile.
 */
public class PlayerMove implements Transition {

    private final Player player;
    private final Direction direction;
    private float progress = 0;

    public PlayerMove(Tile player, Direction direction) {
        this.player = (Player) player;
        this.direction = direction;
        player.setState(TileState.ACTIVE);
    }

    /**
     * Player movement will run before all other transitions, except the
     * BoulderMove transition (the stone is pushed ahead, before the player
     * moves into its place).
     */
    @Override
    public int priority() {
        return 2;
    }

    @Override
    public Result run(Game game) {
        progress += 0.5f;
        Position current = new Position(player.row(), player.col());
        if (progress < 1) {
            Tile target = game.room().get(player.position().move(direction));
            game.draw(current.move(direction, progress), moveImage());
            game.room().set(player.position(), player.current());
            game.room().set(target.position(), player);
            return Result.CONTINUE;
        } else {
            game.draw(current, standImage());
            player.setState(TileState.PASSIVE);

            // If the target is a warp tile, start a warp transition
            if (player.current().type() == ActorType.WARP)
                game.schedule(new Warp(player, game.room()));

            return Result.DONE;
        }
    }

    private Image moveImage() {
        return player.direction() == Direction.RIGHT
                ? Image.PLAYER_RIGHT_MOVE
                : Image.PLAYER_LEFT_MOVE;
    }

    private Image standImage() {
        return player.direction() == Direction.RIGHT
                ? Image.PLAYER_RIGHT_STAND
                : Image.PLAYER_LEFT_STAND;
    }
}
