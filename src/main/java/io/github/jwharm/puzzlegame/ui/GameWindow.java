package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.base.GErrorException;
import io.github.jwharm.javagi.base.Out;
import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.transitions.LoadRoom;
import org.gnome.adw.Application;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.HeaderBar;
import org.gnome.adw.MessageDialog;
import org.gnome.gdk.Gdk;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.gtk.*;

import java.lang.foreign.MemorySegment;

public class GameWindow extends ApplicationWindow {

    private static final Type gtype = Types.register(GameWindow.class);

    private Label levelLabel, livesLabel, scoreLabel, bonusLabel;
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
        controller.onKeyReleased((keyVal, _, _) -> keyReleased(keyVal));
        this.addController(controller);
        this.onCloseRequest(() -> {
            this.getApplication().quit();
            return true;
        });

        this.paintable = GamePaintable.create();

        HeaderBar headerBar = new HeaderBar();
        pauseButton = Button.fromIconName("media-playback-pause");
        var resetButton = Button.fromIconName("view-refresh-symbolic");
        levelLabel = new Label("Level:  ");
        livesLabel = new Label("Lives:  ");
        scoreLabel = new Label("Score:  ");
        bonusLabel = new Label("Bonus:  ");
        headerBar.packStart(pauseButton);
        headerBar.packStart(resetButton);
        headerBar.packStart(levelLabel);
        headerBar.packStart(livesLabel);
        headerBar.packStart(scoreLabel);
        headerBar.packStart(bonusLabel);
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

        resetButton.onClicked(this::reset);
    }

    private void reset() {
        AlertDialog alert = AlertDialog.builder()
                .setModal(true)
                .setMessage("Reset room")
                .setDetail("Do you want to reset the room?")
                .setButtons(new String[] {"Reset", "Continue playing"})
                .setDefaultButton(0)
                .setCancelButton(1)
                .build();

        game().pause();

        // Get dialog result
        alert.choose(this, null, (_, result, _) -> {
            try {
                int button = alert.chooseFinish(result);
                game().resume();
                if (button == 0) {
                    game().schedule(new LoadRoom(false));
                }
            } catch (GErrorException ignored) {} // user clicked cancel
        });
    }

    public void update() {
        if (game().state().message() != null)
            displayMessage();
        paintable.invalidateContents();
        updateHeaderBar();
    }

    private void updateHeaderBar() {
        if (game() != null) {
            updatePauseButton();
            levelLabel.setLabel("Room: " + game().state().room());
            livesLabel.setLabel("Lives: " + game().state().lives());
            scoreLabel.setLabel("Score: " + game().state().score());
            bonusLabel.setLabel("Bonus: " + game().state().bonus());
        }
    }

    private void updatePauseButton() {
        pauseButton.setIconName(game().paused()
                ? "media-playback-start-symbolic"
                : "media-playback-pause-symbolic"
        );
    }

    private void displayMessage() {
        game().pause();
        String message = game().state().message();
        MessageDialog dialog = new MessageDialog(this, null, message);
        dialog.addResponse("continue", "Continue");
        dialog.setDefaultResponse("continue");
        dialog.onResponse(null, _ -> {
            game().state().hideMessage();
            game().resume();
        });
        dialog.present();
    }

    public boolean keyPressed(int keyVal) {
        switch(keyVal) {
            case Gdk.KEY_Left -> game().startMoving(Direction.LEFT);
            case Gdk.KEY_Up -> game().startMoving(Direction.UP);
            case Gdk.KEY_Right -> game().startMoving(Direction.RIGHT);
            case Gdk.KEY_Down -> game().startMoving(Direction.DOWN);
            default -> {}
        }
        return true;
    }

    public void keyReleased(int keyVal) {
        switch(keyVal) {
            case Gdk.KEY_Left -> game().stopMoving(Direction.LEFT);
            case Gdk.KEY_Up -> game().stopMoving(Direction.UP);
            case Gdk.KEY_Right -> game().stopMoving(Direction.RIGHT);
            case Gdk.KEY_Down -> game().stopMoving(Direction.DOWN);
            default -> {}
        }
    }

    public Game game() {
        return paintable.game();
    }

    public void setGame(Game game) {
        paintable.setGame(game);
    }
}
