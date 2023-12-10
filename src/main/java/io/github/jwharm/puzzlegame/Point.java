package io.github.jwharm.puzzlegame;

public record Point(int x, int y) {

    public Point move(Direction direction, int distance) {
        return switch (direction) {
            case UP -> new Point(x - distance, y);
            case DOWN -> new Point(x + distance, y);
            case LEFT -> new Point(x, y - distance);
            case RIGHT -> new Point(x, y + distance);
        };
    }
}
