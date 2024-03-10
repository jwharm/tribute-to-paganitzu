package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.Room;
import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.GameState;
import io.github.jwharm.puzzlegame.io.ImageCache;
import io.github.jwharm.puzzlegame.io.LevelReader;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.GLib;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.adw.Application;
import org.gnome.gtk.Window;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

public class GameApplication extends Application {

    private static final int FPS = 10;

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
        setAccelsForAction("app.quit", new String[]{"<primary>q"});
    }

    @Override
    public void activate() {
        try {
            Window win = this.getActiveWindow();
            if (win == null)
                win = createWindow();
            win.present();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private GameWindow createWindow() {
        GameWindow win = GameWindow.create(this);
        try {
            var game = loadRoom(1);
            win.setGame(game);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        GLib.timeoutAdd(GLib.PRIORITY_DEFAULT, 1000 / FPS, () -> {
            updateGameState(win);
            return true;
        });
        GLib.timeoutAdd(GLib.PRIORITY_DEFAULT, 1000, () -> {
            if (!win.game().paused())
                win.game().state().decreaseBonus();
            return true;
        });
        return win;
    }

    /*
     * This function is run every "frame". It will update the game state, and
     * redraw the screen.
     */
    private void updateGameState(GameWindow win) {
        if (!win.game().paused()) {
            win.game().updateState();
            win.invalidateContents();
        }
    }

    private Game loadRoom(int r) throws IOException {
        ImageCache.init("/home/jw/Documenten/PAGA1/PAGA1.012");
        LevelReader.load("/home/jw/Documenten/PAGA1/PAGA1.007");
        Room room = LevelReader.get(r);
        return new Game(room, new GameState());
    }
}
