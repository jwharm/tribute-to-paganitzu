package io.github.jwharm.puzzlegame.engine;

public record Position(float row, float col) {

    public Position move(Direction direction) {
        return switch (direction) {
            case UP -> new Position(row - 1, col);
            case DOWN -> new Position(row + 1, col);
            case LEFT -> new Position(row, col - 1);
            case RIGHT -> new Position(row, col + 1);
        };
    }

    public Position move(Direction direction, float distance) {
        return switch (direction) {
            case UP -> new Position(row - distance, col);
            case DOWN -> new Position(row + distance, col);
            case LEFT -> new Position(row, col - distance);
            case RIGHT -> new Position(row, col + distance);
        };
    }
}
