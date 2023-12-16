package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.*;
import org.gnome.adw.Application;
import org.gnome.adw.ApplicationWindow;
import org.gnome.gdk.Gdk;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.gtk.ContentFit;
import org.gnome.gtk.EventControllerKey;
import org.gnome.gtk.Picture;

import java.lang.foreign.MemorySegment;

public class GameWindow extends ApplicationWindow {

    private static final Type gtype = Types.register(GameWindow.class);

    private GamePaintable paintable;

    public static Type getType() {
        return gtype;
    }

    public GameWindow(MemorySegment address) {
        super(address);
    }

    public static GameWindow create(Application application) {
        return GObject.newInstance(getType(),
                "application", application,
                "title", "Puzzle game",
                "default-width", 200, "default-height", 200);
    }

    @InstanceInit
    public void init() {
        var controller = new EventControllerKey();
        controller.onKeyPressed((keyVal, _, _) -> keyPressed(keyVal));
        this.addController(controller);
        this.onCloseRequest(() -> {
            this.getApplication().quit();
            return true;
        });

        this.paintable = GamePaintable.create();
        Picture picture = Picture.builder()
                .setPaintable(paintable)
                .setHexpand(true)
                .setVexpand(true)
                .setContentFit(ContentFit.CONTAIN)
                .build();
        this.setContent(picture);
    }

    public void invalidateContents() {
        paintable.invalidateContents();
    }

    public void setGame(Game game) {
        paintable.setGame(game);
    }

    public Game game() {
        return paintable.game();
    }

    public boolean keyPressed(int keyVal) {
        if (game().paused()) return true;
        switch(keyVal) {
            case Gdk.KEY_Left -> game().move(Direction.LEFT);
            case Gdk.KEY_Up -> game().move(Direction.UP);
            case Gdk.KEY_Right -> game().move(Direction.RIGHT);
            case Gdk.KEY_Down -> game().move(Direction.DOWN);
            default -> {}
        }
        return true;
    }
}
