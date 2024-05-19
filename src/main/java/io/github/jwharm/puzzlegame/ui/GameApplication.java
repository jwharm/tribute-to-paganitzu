package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.adw.Application;
import org.gnome.gtk.Window;

import java.lang.foreign.MemorySegment;

import static org.gnome.glib.GLib.SOURCE_CONTINUE;

public class GameApplication extends Application {

    private static final Type gtype = Types.register(GameApplication.class);

    public static Type getType() {
        return gtype;
    }

    public GameApplication(MemorySegment address) {
        super(address);
    }

    public static GameApplication create() {
        return GObject.newInstance(getType(),
                "application-id", "io.github.jwharm.PuzzleGame",
                "flags", ApplicationFlags.DEFAULT_FLAGS);
    }

    @InstanceInit
    public void init() {
        var quit = new SimpleAction("quit", null);
        quit.onActivate(_ -> quit());
        addAction(quit);

        setAccelsForAction("win.pause", new String[]{"Escape"});
        setAccelsForAction("win.restart", new String[]{"<control>r"});
        setAccelsForAction("win.save", new String[]{"<control>s"});
        setAccelsForAction("win.load", new String[]{"<control>l"});
        setAccelsForAction("app.quit", new String[]{"<control>q"});
    }

    @Override
    public void activate() {
        try {
            Window win = this.getActiveWindow();
            if (win == null)
                win = GameWindow.create(this);
            win.present();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
