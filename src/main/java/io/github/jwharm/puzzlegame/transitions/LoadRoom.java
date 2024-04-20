package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Transition;

/**
 * The LoadRoom transition runs the following sequence:
 * 1. Run HideRoom transition
 * 2. Create new Room instance
 * 3. Run RevealRoom transition
 * 4. Run Spawn transition for the player tile
 * <p>
 * The new Room instance is either the same one (when the player died or asked
 * to restart the room), or the next one.
 */
public class LoadRoom implements Transition {

    private final boolean nextRoom;
    private Transition transition;

    /**
     * Create a new LoadRoom transition.
     * @param nextRoom whether to load the next room, or reload the current one
     */
    public LoadRoom(boolean nextRoom) {
        this.nextRoom = nextRoom;
    }

    @Override
    public Result run(Game game) {
        if (transition == null) {
            /*
             * When initially starting the game, there is no existing room to
             * hide. In that case, only reveal the new room.
             */
            if (game.room() == null)
                transition = new RevealRoom();
            else
                transition = new HideRoom();
        }

        Result result = transition.run(game);

        if (result == Result.DONE && transition instanceof HideRoom) {
            if (nextRoom)
                game.state().roomCompleted();
            else
                game.state().reset();
            transition = new RevealRoom();
            return transition.run(game);
        }

        if (result == Result.DONE && transition instanceof RevealRoom) {
            transition = new Spawn(game.room().player());
            return transition.run(game);
        }

        if (result == Result.DONE && transition instanceof Spawn) {
            game.scheduleTransitions();
            game.unfreeze();
        }

        return result;
    }
}
