package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.transitions.PlayerMove;
import org.gnome.gdk.Gdk;
import org.gnome.gdk.ModifierType;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.gtk.*;

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
        controller.onKeyPressed(this::keyPressed);
        this.addController(controller);
        this.onCloseRequest(() -> {
            ((Application) this.getProperty("application")).quit();
            return true;
        });

        paintable = GamePaintable.create();

        var picture = Picture.builder()
                .setPaintable(paintable)
                .setHexpand(true)
                .setVexpand(true)
                .setContentFit(ContentFit.CONTAIN)
                .build();

//        var img = Image.builder()
//                .setPaintable(paintable)
//                .setHexpand(true)
//                .setVexpand(true)
//                .build();
        this.setChild(picture);
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

    public boolean keyPressed(int keyval, int keycode, ModifierType state) {
        String key = Gdk.keyvalName(keyval);
        if (key != null) switch(key) {
            case "Left", "Right", "Up", "Down" -> move(Direction.valueOf(key.toUpperCase()));
        }
        return true;
    }

    private void move(Direction direction) {
        Board board = paintable.game().board();
        Tile player = board.getAny(ActorType.PLAYER);
        if (player == null) throw new IllegalStateException("No player");

        if (player.state() == TileState.ACTIVE) return;

        Tile target = board.get(player.position().move(direction));
        if (target.type() == ActorType.EMPTY)
            paintable.game().schedule(new PlayerMove(player, target, direction));
    }
}
