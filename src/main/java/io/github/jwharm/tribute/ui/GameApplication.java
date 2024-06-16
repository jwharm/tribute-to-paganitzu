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

package io.github.jwharm.tribute.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gobject.types.Types;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.adw.Application;
import org.gnome.gtk.Window;

import java.lang.foreign.MemorySegment;

/**
 * This is the Application subclass for the game. It will install keyboard
 * accelerators and open a new game window.
 */
public class GameApplication extends Application {

    private static final Type gtype = Types.register(GameApplication.class);

    public static Type getType() {
        return gtype;
    }

    /**
     * This constructor is used by Java-GI to create a GameApplication proxy
     * object for an already existing instance in native memory.
     */
    public GameApplication(MemorySegment address) {
        super(address);
    }

    /**
     * Construct a new GameApplication
     */
    public static GameApplication create() {
        return GObject.newInstance(getType(),
                "application-id", "io.github.jwharm.PuzzleGame",
                "flags", ApplicationFlags.DEFAULT_FLAGS);
    }

    /**
     * This method is called by GObject when the new GameApplication instance is
     * constructed. It's used to install the shortcut keys.
     */
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

    /**
     * This method is called during application launch.
     */
    @Override
    public void activate() {
        Window win = this.getActiveWindow();
        if (win == null)
            win = GameWindow.create(this);
        win.present();
    }
}
