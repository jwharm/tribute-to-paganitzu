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

import io.github.jwharm.javagi.gobject.types.Types;
import io.github.jwharm.tribute.engine.Room;
import io.github.jwharm.tribute.engine.GameSession;
import org.freedesktop.cairo.Context;
import org.gnome.gdk.Paintable;
import org.gnome.gdk.PaintableFlags;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.graphene.Rect;
import org.gnome.gtk.Snapshot;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Set;

public class GamePaintable extends GObject implements Paintable {

    public static final int TILE_SIZE = 16;
    private static final Type gtype = Types.register(GamePaintable.class);
    private GameSession game;

    public static Type getType() {
        return gtype;
    }

    public GamePaintable(MemorySegment address) {
        super(address);
    }

    private double calculateScaleFactor(double width, double height) {
        double w = width / Room.WIDTH / TILE_SIZE;
        double h = height / Room.HEIGHT / TILE_SIZE;
        return Math.min(w, h);
    }

    @Override
    public void snapshot(org.gnome.gdk.Snapshot gdkSnapshot,
                         double width,
                         double height) {
        if (game == null) return;

        float w = (float) width;
        float h = (float) height;

        try (var arena = Arena.ofConfined()) {
            double scaling = calculateScaleFactor(width, height);

            Snapshot snapshot = (Snapshot) gdkSnapshot;
            Context cr = snapshot.appendCairo(new Rect(arena).init(0, 0, w, h));

            cr.setSourceRGBA(0.0, 0.0, 0.0, 1.0)
              .rectangle(0, 0, w, h)
              .fill()
              .scale(scaling, scaling);

            for (var cmd : game.drawCommands())
                cmd.draw(cr);

            game.drawCommands().clear();
        }
    }

    @Override
    public Paintable getCurrentImage() {
        return this;
    }

    @Override
    public Set<PaintableFlags> getFlags() {
        return Set.of(PaintableFlags.SIZE); // The image size will never change
    }

    public void setGame(GameSession game) {
        this.game = game;
    }

    public GameSession game() {
        return this.game;
    }

    public static GamePaintable create() {
        return GObject.newInstance(GamePaintable.getType());
    }
}
