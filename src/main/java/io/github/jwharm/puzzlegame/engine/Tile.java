package io.github.jwharm.puzzlegame.engine;

public class Tile {

    public static final Tile OUT_OF_BOUNDS = new Tile(
            (short) 0,
            ActorType.OUT_OF_BOUNDS,
            TileState.PASSIVE,
            Image.EMPTY
    );

    private final short id;
    private final ActorType type;
    private Image image;
    private int row, col;
    private TileState state;
    private Direction direction;

    public Tile(short id, ActorType type, TileState state, Image image) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.image = image;
        this.direction = Direction.RIGHT;
    }

    public short id() {
        return id;
    }

    public ActorType type() {
        return type;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public Position position() {
        return new Position(row, col);
    }

    public TileState state() {
        return state;
    }

    public Direction direction() {
        return direction;
    }

    public Image image() {
        return image;
    }

    public void draw(Game game) {
        game.draw(row, col, image);
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setState(TileState state) {
        this.state = state;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        if (type == ActorType.PLAYER)
            image = switch(direction) {
                case LEFT -> Image.PLAYER_LEFT_STAND;
                case RIGHT -> Image.PLAYER_RIGHT_STAND;
                default -> image;
            };
    }

    @Override
    public String toString() {
        return "Tile %d,%d type=%s id=%d state=%s image=%s direction=%s"
                .formatted(row, col, type, id, state, image, direction);
    }
}
