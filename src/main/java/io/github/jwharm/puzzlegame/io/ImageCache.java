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
