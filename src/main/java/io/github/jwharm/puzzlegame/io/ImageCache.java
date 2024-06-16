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

package io.github.jwharm.puzzlegame.io;

import io.github.jwharm.puzzlegame.engine.Image;
import org.freedesktop.cairo.ImageSurface;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    /**
     * Save all tile images to png files (redundant, not used in game)
     */
    public static void saveImagesToDisk() {
        try {
            Files.createDirectories(Paths.get("build/images"));
            for (int i = 0; i < images.size(); i++) {
                ImageSurface image = images.get(i);
                image.writeToPNG("build/images/%02d.png".formatted(i));
            }
        } catch (Exception ignored) {}
    }
}
