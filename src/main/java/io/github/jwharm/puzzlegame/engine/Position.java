package io.github.jwharm.puzzlegame.engine;

import static java.lang.Math.abs;

public record Position(float row, float col) {

    public Position move(Direction direction) {
        return move(direction, 1);
    }

    public Position move(Direction direction, float distance) {
        return switch (direction) {
            case UP -> new Position(row - distance, col);
            case DOWN -> new Position(row + distance, col);
            case LEFT -> new Position(row, col - distance);
            case RIGHT -> new Position(row, col + distance);
        };
    }

    public boolean borders(Position other) {
        return (this.col == other.col || this.row == other.row)
                && abs(this.col - other.col) <= 1
                && abs(this.row - other.row) <= 1;
    }
}
