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

package io.github.jwharm.tribute.engine;

import io.github.jwharm.tribute.io.ImageCache;
import io.github.jwharm.tribute.io.LevelReader;
import io.github.jwharm.tribute.transitions.*;
import io.github.jwharm.tribute.ui.DrawCommand;
import io.github.jwharm.tribute.ui.Messages;
import org.freedesktop.cairo.Filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import static io.github.jwharm.tribute.ui.GamePaintable.TILE_SIZE;

/**
 * This is the core gameplay class, representing the current gameplay session.
 * <p>
 * When saving the game, this class is serialized to a file, together with the
 * current {@link Room} and {@link GameState}.
 */
public class GameSession implements Serializable {

    /*
     * Random number generator used for randomly trigger gem animations
     * (sparkle effect).
     */
    private final Random RAND = new Random();

    // Global game state
    private final GameState state;
    private Room room;
    private int ticks = 0;

    private boolean paused = false; // Game is paused
    private boolean frozen = true;  // Player is not allowed to move

    private Direction moveDirection = null;

    private final EventQueue eventQueue = new EventQueue();

    // List is transient (not serialized), because it contains lambdas
    private transient List<DrawCommand> drawCommands = new ArrayList<>();

    public GameSession(GameState state) {
        this.state = state;
        schedule(new LoadRoom(LoadRoom.Action.RESET_ROOM));
    }

    // This is called during deserialization (loading a saved game)
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.drawCommands = new ArrayList<>();
    }

    /**
     * Load the room from the levels file. This method is called at the start
     * of the RevealRoom transition.
     */
    public void load() {
        this.room = LevelReader.get(state().room());
        this.moveDirection = null;
    }

    /**
     * Schedule transitions for all actors in the room.
     */
    public void scheduleTransitions() {
        for (var tile : room.getAll()) {
            switch (tile.type()) {
                case SNAKE -> schedule(new SnakeGuard(tile));
                case SPIDER -> schedule(new SpiderMove(tile));
                case SPIKES -> schedule(new SpikeGuard(tile));
                case WATER -> schedule(new WaterFlow(tile));
                case DOOR_LOCKED -> schedule(
                        new DoorLocked(tile, room.getAll(ActorType.KEY)));
                case PIPE -> {
                    // Tile ids 25 & 26 are pipe endings with flowing water
                    if (tile.id() == 25 || tile.id() == 26)
                        schedule(new WaterFlow(tile));
                }
            }
        }
        // Room-specific triggers
        if (state.room() == 14 && !room.player().cursed())
            schedule(new Trigger(ActorType.PLAYER, new Position(1, 11), new Baldness()));
        else if (state.room() == 15 && room.getAny(ActorType.SPIDER) != null)
            schedule(new Trigger(ActorType.GEM, new Position(9, 11), new BrokenPipe()));
        else if (state.room() == 16 && room.get(4, 1).type() != ActorType.BOULDER)
            schedule(new Trigger(ActorType.BOULDER, new Position(4, 1), new WallMove()));
        else if (state.room() == 17 && state().dark())
            schedule(new Trigger(ActorType.PLAYER, new Position(10, 1), new LightSwitch()));
    }

    public void startMoving(Direction direction) {
        this.moveDirection = direction;
    }

    public void stopMoving(Direction direction) {
        if (this.moveDirection == direction)
            this.moveDirection = null;
    }

    /**
     * Move the player in the specified direction.
     */
    public void move() {
        if (moveDirection == null || frozen())
            return; // Not moving

        Player player = room.player();
        if (player == null || player.state() == TileState.ACTIVE)
            return;
        Tile target = room.get(player.position().move(moveDirection));

        // Face in left or right direction
        if (moveDirection == Direction.LEFT || moveDirection == Direction.RIGHT)
            player.setDirection(moveDirection);

        // Determine if we can move in this direction
        boolean canMove = switch(target.type()) {
            case BOULDER -> {
                Tile behind = room.get(target.position().move(moveDirection));
                yield switch (behind.type()) {
                    case EMPTY -> {
                        schedule(new BoulderMove(target, behind, moveDirection), 0);
                        yield true;
                    }
                    case WATER -> {
                        schedule(new BoulderSplash(target, behind, moveDirection), 0);
                        yield true;
                    }
                    default -> false;
                };
            }
            case KEY -> {
                state.keyCollected();
                room.remove(target);
                yield true;
            }
            case GEM -> {
                state.gemCollected();
                room.remove(target);
                yield true;
            }
            case HAT -> {
                player.pickupHat();
                room.remove(target);
                yield true;
            }
            case HIDDEN_PASSAGE -> {
                freeze();
                schedule(new WallCrumble(target));
                yield false;
            }
            case MUD -> {
                room.remove(target);
                yield true;
            }
            case DOOR_UNLOCKED -> {
                freeze();
                state().showMessage(Messages.LEVEL_COMPLETED
                        .formatted(state().bonus()));
                schedule(new LoadRoom(LoadRoom.Action.NEXT_ROOM));
                yield false;
            }
            case EMPTY, WARP -> true;
            default -> false;
        };

        if (canMove)
            schedule(new PlayerMove(player, moveDirection), 0);
    }

    public void draw(Position position, Image image) {
        draw(position.row(), position.col(), image);
    }

    public void draw(float row, float col, Image image) {
        // When the light is off, most images are not painted
        if (state().dark()
                && !LightSwitch.visibleInDark().contains(image))
            return;

        draw((cr) -> {
            cr.setSource(ImageCache.get(image), col * TILE_SIZE, row * TILE_SIZE);
            cr.getSource().setFilter(Filter.NEAREST);
            cr.paint();
        });
    }

    public void draw(DrawCommand cmd) {
        drawCommands.add(cmd);
    }

    public void schedule(Transition transition) {
        schedule(transition, 1);
    }

    public void schedule(Transition transition, int delay) {
        if (transition instanceof LoadRoom)
            eventQueue.schedule(ticks + delay, transition);
        else
            room.eventQueue.schedule(ticks + delay, transition);
    }

    /**
     * The "main loop" function of the game:
     * Move the player, draw all tiles, and run all scheduled transitions.
     */
    public void updateState() {
        ticks++;

        // Subtract a bonus point every second the game is active
        if (ticks % 10 == 0)
            if (! (frozen()))
                state().decreaseBonus();

        // Move the player
        move();

        if (room != null) {
            for (int row = 0; row < Room.HEIGHT; row++) {
                for (int col = 0; col < Room.WIDTH; col++) {
                    Tile tile = room().get(row, col);

                    // Draw passive tile images
                    if (tile.state() == TileState.PASSIVE)
                        tile.draw(this);

                    // Gems sparkle randomly
                    int GEM_SPARKLE_RANDOM_FACTOR = 40;
                    if (!frozen()
                            && tile.type() == ActorType.GEM
                            && tile.state() == TileState.PASSIVE
                            && RAND.nextInt(GEM_SPARKLE_RANDOM_FACTOR) == 0)
                        schedule(new GemSparkle(tile));
                }
            }
        }

        // Run transitions (draws active tile images)
        eventQueue.runTransitions(ticks, this);
        room.eventQueue.runTransitions(ticks, this);
    }

    public Room room() {
        return room;
    }

    public GameState state() {
        return state;
    }

    public int ticks() {
        return ticks;
    }

    public List<DrawCommand> drawCommands() {
        return drawCommands;
    }

    public boolean paused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean frozen() {
        return frozen || paused;
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }
}
