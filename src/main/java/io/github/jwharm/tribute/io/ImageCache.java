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

package io.github.jwharm.tribute.io;

import io.github.jwharm.tribute.engine.Image;
import org.freedesktop.cairo.ImageSurface;

import java.util.List;

/**
 * Helper class that maintains a cache of {@link ImageSurface}s for all tile
 * images.
 */
public class ImageCache {

    private static List<ImageSurface> images;

    public static void init(byte[] data) {
        images = TileReader.loadTileImages(data);
        images.add(TileReader.generateSpikeBarImage());
        images.add(TileReader.generateArrowImage());
    }

    public static ImageSurface get(Image image) {
        return images.get(image.id());
    }
}
