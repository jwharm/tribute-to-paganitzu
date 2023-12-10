package io.github.jwharm.puzzlegame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {

    public static final int TILE_SIZE = 8;
    private final List<Transition> schedule = new ArrayList<>();
    private Board board;
    private int tick = 0;

    public Board board() {
        return board;
    }

    public void draw(Point point, String file) {
        draw(point.x(), point.y(), file);
    }

    public void draw(int x, int y, String file) {

    }

    public void schedule(Transition transition) {
        schedule.add(transition);
    }

    public void advance() {
        tick++;
        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                Tile tile = board().get(row, col);
                if (!tile.isTransitioning()) tile.draw(this);
            }
        }
        schedule.removeIf(transition -> tick % transition.interval() == 0
                && transition.update(this) == Result.DONE);
    }

    public void start() {
        try (ScheduledExecutorService executor = Executors.newScheduledThreadPool(1)) {
            executor.scheduleAtFixedRate(this::advance, 0, 1, TimeUnit.SECONDS);
        }
    }
}
