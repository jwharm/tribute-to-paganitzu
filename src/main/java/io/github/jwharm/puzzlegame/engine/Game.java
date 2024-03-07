package io.github.jwharm.puzzlegame.engine;

import io.github.jwharm.puzzlegame.io.ImageCache;
import io.github.jwharm.puzzlegame.transitions.*;
import io.github.jwharm.puzzlegame.ui.DrawCommand;
import org.freedesktop.cairo.Filter;

import java.util.*;

import static io.github.jwharm.puzzlegame.ui.GamePaintable.TILE_SIZE;

public class Game {

    private record Event(int when, Transition transition) {}

    private final Random RAND = new Random();

    private final Board board;
    private final Queue<Event> transitions = new PriorityQueue<>(
            Comparator.comparing(Event::when).thenComparing(e -> e.transition() instanceof PlayerMove));
    private final List<DrawCommand> drawCommands = new ArrayList<>();
    private final GameState state;
    private int ticks = 0;
    private boolean paused = false;

    public Game(Board board, GameState state) {
        this.board = board;
        this.state = state;
        schedule(new BoardReveal());
    }

    public void startTransitions(Board board) {
        for (var tile : board.getAll())
            switch(tile.type()) {
                case DOOR_LOCKED -> schedule(new DoorLocked(tile, board.getAll(ActorType.KEY)));
                case SNAKE -> schedule(new SnakeGuard(tile));
                case SPIDER -> schedule(new SpiderMove(tile, Direction.RIGHT));
                case WATER -> schedule(new WaterFlow(tile));
            }
    }

    public Board board() {
        return board;
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

    public void move(Direction direction) {
        Tile player = board.player();
        if (player.state() == TileState.ACTIVE) return;
        Tile target = board.get(player.position().move(direction));
        // Face the right way
        if (direction == Direction.LEFT || direction == Direction.RIGHT)
            player.setDirection(direction);
        boolean canMove = target.type() == ActorType.EMPTY;

        switch(target.type()) {
            case BOULDER -> {
                if (board.get(target.position().move(direction)).type() == ActorType.EMPTY) {
                    canMove = true;
                    schedule(new BoulderMove(target, direction));
                }
            }
            case KEY -> {
                state().keyCollected();
                consume(board, target);
                canMove = true;
            }
            case GEM -> {
                state().gemCollected();
                consume(board, target);
                canMove = true;
            }
            case MUD -> {
                consume(board, target);
                canMove = true;
            }
            case DOOR_UNLOCKED -> {
                pause();
                consume(board, target);
                state().win();
            }
        }

        if (canMove) schedule(new PlayerMove(player, direction));
    }

    private void consume(Board board, Tile target) {
        board.replace(target, new Tile(ActorType.EMPTY, TileState.PASSIVE, Image.EMPTY));
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
        transitions.add(new Event(ticks + 1, transition));
    }

    public void updateState() {
        ticks++;
        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                Tile tile = board().get(row, col);

                if (tile.state() == TileState.PASSIVE)
                    tile.draw(this);

                // Gems sparkle randomly
                int GEM_SPARKLE_RANDOM_FACTOR = 20;
                if (tile.type() == ActorType.GEM
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
