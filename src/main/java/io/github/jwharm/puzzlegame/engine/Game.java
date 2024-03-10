package io.github.jwharm.puzzlegame.engine;

import io.github.jwharm.puzzlegame.io.ImageCache;
import io.github.jwharm.puzzlegame.io.LevelReader;
import io.github.jwharm.puzzlegame.transitions.*;
import io.github.jwharm.puzzlegame.ui.DrawCommand;
import org.freedesktop.cairo.Filter;

import java.util.*;

import static io.github.jwharm.puzzlegame.ui.GamePaintable.TILE_SIZE;

public class Game {

    private record Event(int when, Transition transition, int priority) {}

    /*
     * Random number generator used for randomly trigger gem animations
     * (sparkle effect).
     */
    private final Random RAND = new Random();

    /*
     * The queue on which all transitions are scheduled. It is ordered by the
     * requested event time. When multiple events are scheduled in one frame,
     * a `PlayerMove` event will be processed first.
     */
    private final Queue<Event> transitions = new PriorityQueue<>(
            Comparator.comparing(Event::when).thenComparing(Event::priority));

    // Global game state
    private final GameState state;
    private Room room;
    private int ticks = 0;
    private boolean paused = false;
    private boolean frozen = true;

    private final List<DrawCommand> drawCommands = new ArrayList<>();

    public Game(Room room, GameState state) {
        this.room = room;
        this.state = state;
        schedule(new BoardReveal());
    }

    public void resetRoom() {
        this.room = LevelReader.get(state().room());
        transitions.clear();
        schedule(new BoardReveal());
    }

    public void scheduleTransitions(Room room) {
        for (var tile : room.getAll())
            switch(tile.type()) {
                case DOOR_LOCKED -> schedule(new DoorLocked(tile, room.getAll(ActorType.KEY)));
                case SNAKE -> schedule(new SnakeGuard(tile));
                case SPIDER -> schedule(new SpiderMove(tile, Direction.RIGHT));
                case WATER -> schedule(new WaterFlow(tile));
            }
    }

    public Room board() {
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
        return frozen;
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    public void move(Direction direction) {
        Tile player = room.player();
        if (player.state() == TileState.ACTIVE)
            return;
        Tile target = room.get(player.position().move(direction));

        // Face in left or right direction
        if (direction == Direction.LEFT || direction == Direction.RIGHT)
            player.setDirection(direction);

        // Determine if we can move in this direction
        boolean canMove = target.type() == ActorType.EMPTY;
        switch(target.type()) {
            case BOULDER -> {
                if (room.get(target.position().move(direction)).type() == ActorType.EMPTY) {
                    canMove = true;
                    schedule(new BoulderMove(target, direction));
                }
            }
            case KEY -> {
                state().keyCollected();
                consume(room, target);
                canMove = true;
            }
            case GEM -> {
                state().gemCollected();
                consume(room, target);
                canMove = true;
            }
            case MUD -> {
                consume(room, target);
                canMove = true;
            }
            case DOOR_UNLOCKED -> {
                state().goToNextRoom();
                resetRoom();
            }
        }

        if (canMove)
            schedule(new PlayerMove(player, direction));
    }

    private void consume(Room room, Tile target) {
        target.setState(TileState.REMOVED);
        Tile empty = new Tile((short) 0, ActorType.EMPTY, TileState.PASSIVE, Image.EMPTY);
        room.replace(target, empty);
    }

    public void draw(Position position, Image image) {
        draw(position.row(), position.col(), image);
    }

    public void draw(DrawCommand cmd) {
        drawCommands.add(cmd);
    }

    public void draw(float row, float col, Image image) {
        drawCommands.add((cr) -> {
            cr.setSource(ImageCache.get(image), col * TILE_SIZE, row * TILE_SIZE);
            cr.getSource().setFilter(Filter.NEAREST);
            cr.paint();
        });
    }

    public void schedule(Transition transition) {
        transitions.add(new Event(ticks + 1, transition, transition.priority()));
    }

    /**
     * Draw all tiles and run all scheduled transitions.
     */
    public void updateState() {
        ticks++;
        for (int row = 0; row < Room.HEIGHT; row++) {
            for (int col = 0; col < Room.WIDTH; col++) {
                Tile tile = board().get(row, col);

                if (tile.state() == TileState.PASSIVE)
                    tile.draw(this);

                // Gems sparkle randomly
                int GEM_SPARKLE_RANDOM_FACTOR = 20;
                if (!paused()
                        && tile.type() == ActorType.GEM
                        && tile.state() == TileState.PASSIVE
                        && RAND.nextInt(GEM_SPARKLE_RANDOM_FACTOR) == 0)
                    schedule(new GemSparkle(tile));
            }
        }

        while (!transitions.isEmpty() && transitions.peek().when() <= ticks) {
            var transition = transitions.poll().transition();
            if (transition.run(this) == Result.CONTINUE)
                schedule(transition);
        }
    }
}
