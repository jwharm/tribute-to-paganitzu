package io.github.jwharm.puzzlegame.io;

import org.freedesktop.cairo.ImageSurface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ImageCache {

    private static final Path IMAGE_PATH = Path.of("src", "main", "resources", "tiles");
    private static ImageCache instance;
    private final Map<String, ImageSurface> cache = new HashMap<>();

    private ImageCache() {
        try {
            File[] files = IMAGE_PATH.toFile().listFiles();
            if (files == null)
                throw new IOException(STR."No image files found in path \{IMAGE_PATH}");
            for (File file : files) {
                String ref = file.getName().substring(5, 9);
                cache.put(ref, ImageSurface.createFromPNG(file.getAbsolutePath()));
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static ImageCache getInstance() {
        if (instance == null)
            instance = new ImageCache();
        return instance;
    }

    public ImageSurface get(String tileRef) {
        return cache.get(tileRef);
    }
}
