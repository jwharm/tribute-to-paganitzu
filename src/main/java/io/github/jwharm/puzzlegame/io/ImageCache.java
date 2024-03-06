package io.github.jwharm.puzzlegame.io;

import io.github.jwharm.puzzlegame.engine.Image;
import org.freedesktop.cairo.ImageSurface;

import java.util.List;

public class ImageCache {

    private static List<ImageSurface> images;

    public static void init(String filename) {
        images = TileReader.loadTileImages(FileIO.readFile(filename));
    }

    public static ImageSurface get(Image image) {
        return images.get(image.id());
    }
}
