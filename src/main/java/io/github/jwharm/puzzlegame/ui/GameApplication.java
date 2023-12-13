package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.Board;
import io.github.jwharm.puzzlegame.engine.Game;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.GLib;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.gtk.Application;
import org.gnome.gtk.Window;

import java.lang.foreign.MemorySegment;

public class GameApplication extends Application {

    private static final int FPS = 8;

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
        quit.onActivate($ -> quit());
        addAction(quit);
        setAccelsForAction("app.quit", new String[]{"<primary>q"});
    }

    @Override
    public void activate() {
        Window win = this.getActiveWindow();
        if (win == null) {
            win = GameWindow.create(this);
            GameWindow gameWindow = (GameWindow) win;
            var game = stub();
            gameWindow.setGame(game);
            GLib.timeoutAdd(1000 / FPS, () -> advance(gameWindow));
        }
        win.present();
    }

    private boolean advance(GameWindow win) {
        win.game().advance();
        win.invalidateContents();
        return true;
    }

    private Game stub() {
        Board level1 = new Board("""
                ================
                =  P        ~~~=
                =           ~~~=
                =    = =       =
                =    = =*      =
                =    = =       =
                =    ===       =
                =    =         =
                =    =         =
                =              =
                =              =
                ================
                """);
        return new Game(level1);
    }
}
