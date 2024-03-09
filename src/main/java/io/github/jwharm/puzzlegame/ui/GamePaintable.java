package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.Room;
import io.github.jwharm.puzzlegame.engine.Game;
import org.freedesktop.cairo.Context;
import org.gnome.gdk.Paintable;
import org.gnome.gdk.PaintableFlags;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.graphene.Rect;
import org.gnome.gtk.Snapshot;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class GamePaintable extends GObject implements Paintable {

    public static final int TILE_SIZE = 16;
    private static final Type gtype = Types.register(GamePaintable.class);
    private Game game;

    public static Type getType() {
        return gtype;
    }

    public GamePaintable(MemorySegment address) {
        super(address);
    }

    private float calculateScaleFactor(float width, float height) {
        float w = width / Room.WIDTH / TILE_SIZE;
        float h = height / Room.HEIGHT / TILE_SIZE;
        return Math.min(w, h);
    }

    @Override
    public void snapshot(org.gnome.gdk.Snapshot gdkSnapshot, double width, double height) {
        if (game == null) return;
        try (var arena = Arena.ofConfined()) {
            float scaling = calculateScaleFactor((float) width, (float) height);

            Snapshot snapshot = (Snapshot) gdkSnapshot;
            Context cr = snapshot.appendCairo(Rect.allocate(arena).init(0, 0, (float) width, (float) height));
            cr.setSourceRGBA(0.0f, 0.0f, 0.0f, 1.0f)
              .rectangle(0, 0, (float) width, (float) height)
              .fill();

            cr.scale(scaling, scaling);

            for (var cmd : game.drawCommands()) {
                cmd.draw(cr);
            }
            game.drawCommands().clear();
        }
    }

    @Override
    public Paintable getCurrentImage() {
        return this;
    }

    @Override
    public PaintableFlags getFlags() {
        return PaintableFlags.SIZE; // The image size will never change
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game game() {
        return this.game;
    }

    // Add a simple constructor
    public static GamePaintable create() {
        return GObject.newInstance(GamePaintable.getType());
    }
}
