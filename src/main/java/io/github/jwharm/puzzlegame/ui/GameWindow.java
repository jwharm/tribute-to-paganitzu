/* Tribute to Paganitzu, a simple puzzle-game engine
 * Copyright (C) 2024 Jan-Willem Harmannij
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.base.GErrorException;
import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.types.Types;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.io.ArchiveReader;
import io.github.jwharm.puzzlegame.io.ImageCache;
import io.github.jwharm.puzzlegame.io.LevelReader;
import io.github.jwharm.puzzlegame.transitions.LoadRoom;
import org.gnome.adw.*;
import org.gnome.adw.Application;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.MessageDialog;
import org.gnome.gdk.Gdk;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.GLib;
import org.gnome.glib.Type;
import org.gnome.glib.Variant;
import org.gnome.gobject.GObject;
import org.gnome.gtk.*;
import org.gnome.gtk.AlertDialog;

import java.io.*;
import java.lang.foreign.MemorySegment;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.gnome.glib.GLib.SOURCE_CONTINUE;

@GtkTemplate(name="GameWindow", ui="/io/github/jwharm/puzzlegame/GameWindow.ui")
public class GameWindow extends ApplicationWindow {

    private static final int FPS = 10;

    private static final Type gtype = Types.register(GameWindow.class);

    @GtkChild public Label levelLabel;
    @GtkChild public Label livesLabel;
    @GtkChild public Label scoreLabel;
    @GtkChild public Label bonusLabel;
    @GtkChild public Stack stack;
    @GtkChild public ToastOverlay toastOverlay;
    @GtkChild public Picture picture;

    private GamePaintable paintable;
    private Toast pauseMessage;
    private SimpleAction pauseAction;
    private SimpleAction restartAction;
    private SimpleAction loadAction;
    private SimpleAction saveAction;

    public static Type getType() {
        return gtype;
    }

    public GameWindow(MemorySegment address) {
        super(address);
    }

    public static GameWindow create(Application application) {
        return GObject.newInstance(getType(), "application", application);
    }

    @InstanceInit
    public void init() {
        // Add EventController to capture keyboard events
        var controller = new EventControllerKey();
        controller.onKeyPressed((keyVal, _, _) -> keyPressed(keyVal));
        controller.onKeyReleased((keyVal, _, _) -> keyReleased(keyVal));
        this.addController(controller);

        // Quit the game when the window is closed
        this.onCloseRequest(() -> {
            this.getApplication().quit();
            return true;
        });

        // Create and register actions
        var downloadAction = new SimpleAction("download", null);
        downloadAction.onActivate(_ -> download());
        addAction(downloadAction);

        restartAction = new SimpleAction("restart", null);
        restartAction.onActivate(_ -> restart());
        restartAction.setEnabled(false);
        addAction(restartAction);

        loadAction = new SimpleAction("load", null);
        loadAction.onActivate(_ -> load());
        loadAction.setEnabled(false);
        addAction(loadAction);

        saveAction = new SimpleAction("save", null);
        saveAction.onActivate(_ -> save());
        saveAction.setEnabled(false);
        addAction(saveAction);

        var boolVariant = Variant.boolean_(false);
        pauseAction = SimpleAction.stateful("pause", null, boolVariant);
        pauseAction.onActivate(_ -> pauseOrResume(pauseAction));
        pauseAction.setEnabled(false);
        addAction(pauseAction);

        var aboutAction = new SimpleAction("about", null);
        aboutAction.onActivate(_ -> about());
        aboutAction.setEnabled(true);
        addAction(aboutAction);

        // Create the Toast that is displayed when the game is paused
        pauseMessage = Toast.builder()
                .setTitle("Game paused")
                .setActionName("win.pause")
                .setButtonLabel("Resume")
                .setTimeout(0)
                .build();

        // Create the GamePaintable widget
        paintable = GamePaintable.create();
        picture.setPaintable(paintable);

        // If the game files are downloaded and cached, proceed to the game.
        if (isCached())
            initGame();
    }

    private void pauseOrResume(SimpleAction pauseAction) {
        if (game().paused()) {
            game().resume();
            pauseMessage.dismiss();
        } else {
            game().pause();
            toastOverlay.addToast(pauseMessage);
        }
        pauseAction.setState(Variant.boolean_(game().paused()));
    }

    private void restart() {
        AlertDialog alert = AlertDialog.builder()
                .setModal(true)
                .setMessage("Restart room")
                .setDetail("Do you want to restart the room?")
                .setButtons(new String[] {"Restart", "Continue playing"})
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
                    game().schedule(new LoadRoom(LoadRoom.Action.RESET_ROOM));
                }
            } catch (GErrorException ignored) {} // user clicked cancel
        });
    }

    private void save() {
        Game game = game();
        game.pause();
        try (var fos = new FileOutputStream(getSaveGameFileName().toFile());
             var zos = new DeflaterOutputStream(fos);
             var oos = new ObjectOutputStream(zos)) {
            oos.writeObject(game);
            toastOverlay.addToast(
                    Toast.builder()
                         .setTitle("Game saved")
                         .setTimeout(1)
                         .build());
        } catch (IOException ioe) {
            toastOverlay.addToast(new Toast(ioe.getMessage()));
        }
        game.resume();
    }

    private void load() {
        try (var fis = new FileInputStream(getSaveGameFileName().toFile());
             var zis = new InflaterInputStream(fis);
             var ois = new ObjectInputStream(zis)) {
            Game game = (Game) ois.readObject();
            paintable.setGame(game);
            game.schedule(new LoadRoom(LoadRoom.Action.NO_ACTION));
            game.freeze();
            game.resume();
        } catch (ClassNotFoundException ignored) {
        } catch (IOException ioe) {
            toastOverlay.addToast(new Toast(ioe.getMessage()));
        }
    }

    private void about() {
        var aboutWindow = AboutWindow.builder()
                .setApplicationName("Tribute to Paganitzu")
                .setVersion("1.0.0")
                .setCopyright("© 2024 Jan-Willem Harmannij")
                .setLicenseType(License.GPL_3_0)
                .setIssueUrl("https://github.com/jwharm/puzzlegame/issues")
                .build();
        aboutWindow.addLegalSection(
                "Paganitzu shareware game data files",
                "© 1991 by Trilobyte",
                License.CUSTOM,
                "This game uses data files from the shareware episode of the " +
                "game \"Paganitzu\" created by Keith Schuler and published " +
                "in 1991 by Apogee Software. Both the shareware episode and " +
                "the full game are available from 3DRealms."
        );
        aboutWindow.present();
    }

    private Path getSaveGameFileName() throws IOException {
        String userDataDir = GLib.getUserDataDir();
        Path path = Path.of(userDataDir, "Tribute to Paganitzu");
        Files.createDirectories(path);
        return path.resolve("Saved game");
    }

    private Path getCachedFileName() {
        String userCacheDir = GLib.getUserCacheDir();
        return Path.of(userCacheDir, "PAGA1.zip");
    }

    private boolean isCached() {
        return getCachedFileName().toFile().exists();
    }

    private void download() {
        try {
            String url = "https://archive.org/download/Paganitzu/PAGA.zip";
            URLConnection connection = URI.create(url).toURL().openConnection();
            InputStream inputStream = connection.getInputStream();
            Path cachedFile = getCachedFileName();
            Files.copy(inputStream, cachedFile, REPLACE_EXISTING);
            inputStream.close();
            initGame();
        } catch (Exception e) {
            MessageDialog dialog = new MessageDialog(this, null, e.getMessage());
            dialog.addResponse("ok", "OK");
            dialog.setDefaultResponse("ok");
            dialog.present();
        }
    }

    /*
     * This is run after the game asset files have been located by the player.
     * We switch to the Paintable, load the assets, and launch level 1.
     */
    private void initGame() {
        try {
            ArchiveReader reader = new ArchiveReader();
            String path = getCachedFileName().toString();
            System.out.println(path);
            var map = reader.extractGameAssets(path);

            ImageCache.init(map.get("PAGA1.012"));
            LevelReader.setData(map.get("PAGA1.007"));
        } catch (IOException ioe) {
            MessageDialog dialog = new MessageDialog(this, null, ioe.getMessage());
            dialog.addResponse("ok", "OK");
            dialog.setDefaultResponse("ok");
            dialog.present();
            return;
        }

        stack.setVisibleChild(toastOverlay);
        for (var action : List.of(pauseAction, restartAction, loadAction, saveAction))
            action.setEnabled(true);

        var game = new Game(new GameState(1, 5, 0));
        paintable.setGame(game);

        GLib.timeoutAdd(GLib.PRIORITY_DEFAULT, 1000 / FPS, () -> {
            updateAndRedraw();
            return SOURCE_CONTINUE;
        });
    }

    /*
     * This function is run every "frame". It will update the game state, and
     * redraw the screen.
     */
    private void updateAndRedraw() {
        Game game = game();
        if (game != null && !game.paused()) {
            game.updateState();
            if (game.state().message() != null)
                displayMessage();
            paintable.invalidateContents();
            updateHeaderBar();
        }
    }

    private void updateHeaderBar() {
        if (game() != null) {
            levelLabel.setLabel("Room: " + game().state().room());
            livesLabel.setLabel("Lives: " + game().state().lives());
            scoreLabel.setLabel("Score: " + game().state().score());
            bonusLabel.setLabel("Bonus: " + game().state().bonus());
        }
    }

    /*
     * Pause the game and display a message dialog. When the dialog is
     * dismissed, the game is resumed.
     * The actual message is stored in the GameState class.
     */
    private void displayMessage() {
        game().pause();
        String message = game().state().message();
        MessageDialog dialog = new MessageDialog(this, null, message);
        dialog.addResponse("continue", "Continue");
        dialog.setDefaultResponse("continue");
        dialog.onResponse(null, _ -> {
            game().state().clearMessage();
            game().resume();
        });
        dialog.present();
    }

    private boolean keyPressed(int keyVal) {
        if (game() == null)
            return Gdk.EVENT_PROPAGATE;

        switch(keyVal) {
            case Gdk.KEY_Left -> game().startMoving(Direction.LEFT);
            case Gdk.KEY_Up -> game().startMoving(Direction.UP);
            case Gdk.KEY_Right -> game().startMoving(Direction.RIGHT);
            case Gdk.KEY_Down -> game().startMoving(Direction.DOWN);
            default -> {}
        }
        return Gdk.EVENT_STOP;
    }

    private void keyReleased(int keyVal) {
        if (game() == null)
            return;

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
}
