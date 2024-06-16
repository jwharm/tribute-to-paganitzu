/* Tribute to Paganitzu, a simple puzzle-game engine
 * Copyright (C) 2024 Jan-Willem Harmannij
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.jwharm.puzzlegame.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
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
public class EventQueue implements Serializable {

    public record Event(int when, Transition transition, int priority) {}

    /*
     * The queue on which all transitions are scheduled. It is ordered by the
     * requested event time. When multiple events are scheduled in one frame,
     * events with the lowest priority number will be processed first.
     *
     * It is transient (not serialized) because it contains lambdas. During
     * deserialization (loading a saved game) a new instance is created.
     */
    private transient PriorityQueue<Event> transitions;

    public EventQueue() {
        createQueue();
    }

    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        createQueue();
    }

    private void createQueue() {
        transitions = new PriorityQueue<>(
                Comparator.comparing(Event::when).thenComparing(Event::priority));
    }

    public void schedule(int when, Transition transition) {
        transitions.add(new Event(when, transition, transition.priority()));
    }

    /**
     * Run all transitions that are enqueued for the requested frame. When a
     * transition returns {@link Result#CONTINUE} it is enqueued again, to be
     * run in the next frame.
     */
    public void runTransitions(int when, GameSession game) {
        while (!transitions.isEmpty() && transitions.peek().when() <= when) {
            var transition = transitions.poll().transition();
            if (transition.run(game) == Result.CONTINUE)
                schedule(when + 1, transition);
        }
    }
}
