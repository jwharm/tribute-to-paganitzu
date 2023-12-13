package io.github.jwharm.puzzlegame.engine;

import io.github.jwharm.puzzlegame.transitions.SpiderMove;
import io.github.jwharm.puzzlegame.transitions.WaterFlow;
import io.github.jwharm.puzzlegame.ui.DrawCommand;

import java.util.*;

public class Game {

    private record Event(int when, Transition transition) {}

    private final Board board;
    private final Queue<Event> transitions = new PriorityQueue<>(Comparator.comparingInt(Event::when));
    private final List<DrawCommand> drawCommands = new ArrayList<>();
    private int tick = 0;

    public Game(Board board) {
        this.board = board;
        for (var spider : board.getAll(ActorType.SPIDER)) {
            schedule(new SpiderMove(spider, board.get(spider.position().move(Direction.DOWN)), Direction.DOWN));
        }
        for (var water : board.getAll(ActorType.WATER)) {
            schedule(new WaterFlow(water));
        }
    }

    public Board board() {
        return board;
    }

    public int tick() {
        return tick;
    }

    public List<DrawCommand> drawCommands() {
        return drawCommands;
    }

    public void draw(Position position, String file) {
        draw(position.row(), position.col(), file);
    }

    public void draw(float row, float col, String file) {
        drawCommands.add(new DrawCommand(row, col, file));
    }

    public void schedule(Transition transition) {
        transitions.add(new Event(tick + 1, transition));
    }

    public void advance() {
        tick++;
        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                Tile tile = board().get(row, col);
                if (tile.state() == TileState.PASSIVE) tile.draw(this);
            }
        }

        while (!transitions.isEmpty() && transitions.peek().when() <= tick) {
            var transition = transitions.poll().transition();
            if (transition.run(this) == Result.CONTINUE)
                schedule(transition);
        }
    }
}
