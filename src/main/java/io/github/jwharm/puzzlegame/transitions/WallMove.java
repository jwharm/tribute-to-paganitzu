package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.*;

/**
 * Move a series of walls in the specified direction. The first 4 iterations,
 * two adjacent tiles will move both at the same time. Then during the last 2
 * iterations, a single tile will move.
 */
public class WallMove implements Transition {

    private record Move(Position first, Position second, Direction direction) {}

    private static final Move[] moves = {
            new Move(new Position(4, 3), new Position(4, 2), Direction.RIGHT),
            new Move(new Position(4, 6), new Position(4, 5), Direction.RIGHT),
            new Move(new Position(4, 9), new Position(4, 8), Direction.RIGHT),
            new Move(new Position(4, 12), new Position(4, 11), Direction.RIGHT),
            new Move(new Position(4, 14), null, Direction.RIGHT),
            new Move(new Position(1, 9), null, Direction.DOWN)
    };

    private int currentMove = 0;
    private float progress = 0;

    @Override
    public Result run(Game game) {
        var m = moves[currentMove];

        // When the transition starts
        if (currentMove == 0 && progress == 0) {
            game.freeze();
        }

        // Start moving the next pair of tiles
        if (progress == 0) {
            game.room().get(m.first).setState(TileState.ACTIVE);
            if (m.second != null)
                game.room().get(m.second).setState(TileState.ACTIVE);
        }

        // This defines the speed with which the tiles move
        progress += 0.1f;

        if (progress < 1) {
            // Mid-move: draw the tiles half-way
            game.draw(m.first.move(m.direction, progress), game.room().get(m.first).image());
            if (m.second != null)
                game.draw(m.second.move(m.direction, progress), game.room().get(m.second).image());
            return Result.CONTINUE;
        } else {
            // Movement complete: Update the tile locations
            doMove(game, m.first, m.first.move(m.direction));
            if (m.second != null)
                doMove(game, m.second, m.second.move(m.direction));

            // Start the next iteration
            currentMove++;
            progress = 0;

            if (currentMove == moves.length) {
                game.unfreeze();
                return Result.DONE;
            } else {
                return Result.CONTINUE;
            }
        }
    }

    private void doMove(Game game, Position src, Position dest) {
        game.room().remove(game.room().get(dest));
        game.room().swap(game.room().get(src), game.room().get(dest));
        game.draw(dest, game.room().get(dest).image());
        game.room().get(dest).setState(TileState.PASSIVE);
    }
}
