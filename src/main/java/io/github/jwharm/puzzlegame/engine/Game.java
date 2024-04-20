package io.github.jwharm.puzzlegame.engine;

import io.github.jwharm.puzzlegame.io.ImageCache;
import io.github.jwharm.puzzlegame.io.LevelReader;
import io.github.jwharm.puzzlegame.transitions.*;
import io.github.jwharm.puzzlegame.ui.DrawCommand;
import org.freedesktop.cairo.Filter;

import java.util.*;

import static io.github.jwharm.puzzlegame.ui.GamePaintable.TILE_SIZE;

public class Game {

    /*
     * Random number generator used for randomly trigger gem animations
     * (sparkle effect).
     */
    private final Random RAND = new Random();

    // Global game state
    private final GameState state;
    private Room room;
    private int ticks = 0;
    private boolean paused = false;
    private boolean frozen = true;
    private Direction moveDirection = null;
    private final EventQueue eventQueue = new EventQueue();
    private final List<DrawCommand> drawCommands = new ArrayList<>();

    public Game(GameState state) {
        this.state = state;
        schedule(new LoadRoom(false));
    }

    /**
     * Load the room from the levels file. This method is called at the start
     * of the RevealRoom transition.
     */
    public void load() {
        this.room = LevelReader.get(state().room());
        room.printToStdOut();
    }

    /**
     * Schedule transitions for all actors in the room.
     */
    public void scheduleTransitions(Room room) {
        for (var tile : room.getAll()) {
            switch (tile.type()) {
                case DOOR_LOCKED -> schedule(new DoorLocked(tile, room.getAll(ActorType.KEY)));
                case SNAKE -> schedule(new SnakeGuard(tile));
                case SPIDER -> schedule(new SpiderMove(tile));
                case SPIKES -> schedule(new SpikeGuard(tile));
                case WATER -> schedule(new WaterFlow(tile));
                case PIPE -> {
                    // Tile ids 25 & 26 are pipe endings with flowing water
                    if (tile.id() == 25 || tile.id() == 26)
                        schedule(new WaterFlow(tile));
                }
            }
        }
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

        Tile player = room.player();
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
            case HIDDEN_PASSAGE -> {
                schedule(new WallCrumble(target));
                yield false;
            }
            case MUD -> {
                room.remove(target);
                yield true;
            }
            case DOOR_UNLOCKED -> {
                freeze();
                schedule(new LoadRoom(true));
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
