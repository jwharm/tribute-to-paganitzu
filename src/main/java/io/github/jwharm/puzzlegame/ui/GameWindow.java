package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.base.Out;
import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.*;
import org.gnome.adw.Application;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.HeaderBar;
import org.gnome.gdk.Gdk;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.gtk.*;

import java.lang.foreign.MemorySegment;

public class GameWindow extends ApplicationWindow {

    private static final Type gtype = Types.register(GameWindow.class);

    private Label levelLabel, livesLabel, scoreLabel;
    private Button pauseButton;
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
                "default-width", 580,
                "default-height", 480);
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

        HeaderBar headerBar = new HeaderBar();
        pauseButton = Button.fromIconName("media-playback-pause");
        levelLabel = new Label("Level: 0");
        livesLabel = new Label("Lives: 0");
        scoreLabel = new Label("Score: 0");
        headerBar.packStart(pauseButton);
        headerBar.packStart(levelLabel);
        headerBar.packStart(livesLabel);
        headerBar.packStart(scoreLabel);
        updateHeaderBar();

        Picture picture = Picture.builder()
                .setPaintable(paintable)
                .setHexpand(true)
                .setVexpand(true)
                .setContentFit(ContentFit.CONTAIN)
                .setWidthRequest(Room.WIDTH * GamePaintable.TILE_SIZE)
                .setHeightRequest(Room.HEIGHT * GamePaintable.TILE_SIZE)
                .build();

        Grid grid = new Grid();
        grid.attach(headerBar, 0, 0, 1, 1);
        grid.attach(picture, 0, 1, 1, 1);
        this.setContent(grid);

        this.onNotify("default-height", _ -> {
            int headerBarHeight = headerBar.getHeight();
            if (headerBarHeight == 0) headerBarHeight = 47;
            Out<Integer> w = new Out<>();
            this.getDefaultSize(w, null);
            this.setDefaultSize(w.get(), ((int) (w.get() * 0.75)) + headerBarHeight);
        });

        pauseButton.onClicked(() -> {
            if (game().paused()) game().resume(); else game().pause();
            updatePauseButton();
        });
    }

    private void updatePauseButton() {
        pauseButton.setIconName(game().paused()
                ? "media-playback-start-symbolic"
                : "media-playback-pause-symbolic"
        );
    }

    public void invalidateContents() {
        paintable.invalidateContents();
        updateHeaderBar();
    }

    private void updateHeaderBar() {
        if (game() != null) {
            updatePauseButton();
            levelLabel.setLabel("Room: " + game().state().level());
            livesLabel.setLabel("Lives: " + game().state().lives());
            scoreLabel.setLabel("Score: " + game().state().score());
        }
    }

    public void setGame(Game game) {
        paintable.setGame(game);
    }

    public Game game() {
        return paintable.game();
    }

    public boolean keyPressed(int keyVal) {
        if (game().paused() || game().frozen())
            return true;

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
