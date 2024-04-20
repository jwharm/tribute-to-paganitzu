package io.github.jwharm.puzzlegame.engine;

/**
 * This transition will wait until the requested type is in the designated spot,
 * to trigger a transition event.
 */
public class Trigger implements Transition {

    private final ActorType type;
    private final Position spot;
    private final Transition event;

    public Trigger(ActorType type, Position spot, Transition event) {
        this.type = type;
        this.spot = spot;
        this.event = event;
    }

    @Override
    public Result run(Game game) {
        if (game.room().get(spot).type() == type) {
            game.schedule(event);
            return Result.DONE;
        } else {
            return Result.CONTINUE;
        }
    }
}
