package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.Board;
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

    public GamePaintable(MemorySegment address) {
        super(address);
    }

    private static final Type gtype = Types.register(GamePaintable.class);

    public static Type getType() {
        return gtype;
    }

    private Game game;

    private float calculateTileSize(float width, float height) {
        float boardRatio = (float)Board.HEIGHT / (float)Board.WIDTH;
        float windowRatio = height / width;
        float tileSize = (windowRatio < boardRatio) ? (height / (float) Board.HEIGHT) : (width / (float) Board.WIDTH);
        return (float) Math.floor(tileSize);
    }

    @Override
    public void snapshot(org.gnome.gdk.Snapshot gdkSnapshot, double width, double height) {
        if (game == null) return;
        try (var arena = Arena.ofConfined()) {
            float tileSize = calculateTileSize((float) width, (float) height);

            Snapshot snapshot = (Snapshot) gdkSnapshot;
            Context ctx = snapshot.appendCairo(Rect.allocate(arena).init(0, 0, (float) width, (float) height));
            ctx.setSourceRGBA(0.0f, 0.0f, 0.0f, 1.0f)
               .rectangle(0, 0, (float) width, (float) height)
               .fill();

            for (var cmd : game.drawCommands()) {
                if (cmd.file().toLowerCase().startsWith("empty"))
                    ctx.setSourceRGBA(0.0f, 0.0f, 0.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("door_locked"))
                    ctx.setSourceRGBA(0.4f, 0.2f, 0.2f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("door_unlocked"))
                    ctx.setSourceRGBA(0.8f, 0.8f, 8.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("gem"))
                    ctx.setSourceRGBA(0.5f, 0.5f, 1.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("player"))
                    ctx.setSourceRGBA(1.0f, 1.0f, 1.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("key"))
                    ctx.setSourceRGBA(1.0f, 1.0f, 0.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("wall"))
                    ctx.setSourceRGBA(0.6f, 0.3f, 0.3f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("mud"))
                    ctx.setSourceRGBA(0.2f, 0.1f, 0.1f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("spider"))
                    ctx.setSourceRGBA(1.0f, 0.0f, 0.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("venom"))
                    ctx.setSourceRGBA(1.0f, 1.0f, 0.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("water0"))
                    ctx.setSourceRGBA(0.0f, 0.0f, 1.0f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("water1"))
                    ctx.setSourceRGBA(0.1f, 0.1f, 0.8f, 1.0f);
                else if (cmd.file().toLowerCase().startsWith("boulder"))
                    ctx.setSourceRGBA(0.6f, 0.3f, 0.3f, 1.0f);
                else
                    ctx.setSourceRGBA(0.0f, 1.0f, 0.0f, 1.0f);

                if (cmd.file().toLowerCase().startsWith("boulder"))
                    ctx.arc(cmd.col() * tileSize + (tileSize/2), cmd.row() * tileSize + (tileSize/2), tileSize/2, 0.0, 2.0 * Math.PI).fill();
                else
                    ctx.rectangle(cmd.col() * tileSize, cmd.row() * tileSize, tileSize, tileSize).fill();
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
