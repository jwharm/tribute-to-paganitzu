package io.github.jwharm.puzzlegame.engine;

/**
 * Technically the player is a tile like any other, but we have to keep track
 * of the tile currently "below" it, so that when the player moves, it is
 * restored.
 */
public final class Player extends Tile {

    private Tile current;
    private boolean cursed = false;
    private boolean bald = false;

    public Player(short id, ActorType type, TileState state, Image image) {
        super(id, type, state, image);
    }

    private void updateImage() {
        image = switch (direction()) {
            case LEFT -> bald ? Image.PLAYER_NO_HAT_LEFT_STAND : Image.PLAYER_LEFT_STAND;
            case RIGHT -> bald ? Image.PLAYER_NO_HAT_RIGHT_STAND : Image.PLAYER_RIGHT_STAND;
            default -> image;
        };
    }

    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        updateImage();
    }

    public void setCurrent(Tile tile) {
        current = tile;
    }

    public Tile current() {
        return current;
    }

    public boolean cursed() {
        return cursed;
    }

    public boolean bald() {
        return bald;
    }

    public void curse() {
        cursed = true;
    }

    public void looseHat() {
        bald = true;
        updateImage();
    }

    public void pickupHat() {
        bald = false;
        updateImage();
    }
}
