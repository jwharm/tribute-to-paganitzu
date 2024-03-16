package io.github.jwharm.puzzlegame.engine;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * An EventQueue contains transitions: things that happen in the game on a
 * specified moment. Most transitions draw something on the screen, or trigger
 * a reaction in the game. Transitions that run during multiple game frames
 * (like animations), are automatically enqueued after every invocation, until
 * they have finished.
 * <p>
 * There are two event queue instances: One in the Game class, and one in the
 * Room class. Most transitions are specific to a Room instance, so they are on
 * that queue. A LoadRoom transition is enqueued on the queue in the Game
 * class. During the LoadRoom transition, the entire room is replace with a new
 * instance, and the room queue will be empty.
 */
public class EventQueue {

    public record Event(int when, Transition transition, int priority) {}

    /*
     * The queue on which all transitions are scheduled. It is ordered by the
     * requested event time. When multiple events are scheduled in one frame,
     * events with the lowest priority number will be processed first.
     */
    private final PriorityQueue<Event> transitions = new PriorityQueue<>(
            Comparator.comparing(Event::when).thenComparing(Event::priority));

    public void schedule(int when, Transition transition) {
        transitions.add(new Event(when, transition, transition.priority()));
    }

    /**
     * Run all transitions that are enqueued for the requested frame. When a
     * transition returns {@link Result#CONTINUE} it is enqueued again, to be
     * run in the next frame.
     */
    public void runTransitions(int when, Game game) {
        while (!transitions.isEmpty() && transitions.peek().when() <= when) {
            var transition = transitions.poll().transition();
            if (transition.run(game) == Result.CONTINUE)
                schedule(when + 1, transition);
        }
    }
}
