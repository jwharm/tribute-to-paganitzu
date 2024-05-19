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

    public enum Action {
        NO_ACTION,
        RESET_ROOM,
        NEXT_ROOM
    }

    private final Action action;
    private Transition transition;

    /**
     * Create a new LoadRoom transition.
     * @param action whether to load the next room, or reload the current one
     */
    public LoadRoom(Action action) {
        this.action = action;
    }

    @Override
    public Result run(Game game) {
        if (transition == null) {
            /*
             * When initially starting the game, there is no existing room to
             * hide. In that case, only reveal the new room.
             */
            if (game.room() == null) {
                if (action != Action.NO_ACTION)
                    game.load();
                transition = new RevealRoom();

            } else {
                transition = new HideRoom();
            }
        }

        Result result = transition.run(game);

        if (result == Result.DONE && transition instanceof HideRoom) {
            if (action == Action.NEXT_ROOM)
                game.state().roomCompleted();
            else if (action == Action.RESET_ROOM)
                game.state().reset();
            if (action != Action.NO_ACTION)
                game.load();
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
