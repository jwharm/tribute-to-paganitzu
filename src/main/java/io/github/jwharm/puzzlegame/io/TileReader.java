package io.github.jwharm.puzzlegame.io;

import org.freedesktop.cairo.Format;
import org.freedesktop.cairo.ImageSurface;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to read images a Paganitzu data file.
 * <p>
 * Credits to user <a href="https://moddingwiki.shikadi.net/wiki/User:K1n9_Duk3">K1n9_Duk3</a>
 * from the DOS Game Modding Wiki for documenting the
 * <a href="https://moddingwiki.shikadi.net/wiki/Paganitzu_Graphics_Format">Paganitzu data file format</a>.
 */
public class TileReader {

    /**
     * Create a list of ImageSurfaces from the contents of a Paganitzu EGA tile images file.
     * For episode 1, this is the PAGA1.007 file.
     *
     * @param  data the file contents
     * @return list of ImageSurfaces, ordered the same as in the file
     */
    public static List<ImageSurface> loadTileImages(byte[] data) {
        // Skip the BASIC BSAVE/BLOAD header (7 bytes)
        int offset = 7;
        int length = data.length - offset;

        // The image data consist of INT16LE values.
        ShortBuffer buffer = ByteBuffer.wrap(data, offset, length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();

        // Current position in the ShortBuffer
        int pos = 0;

        // The loaded image surfaces
        List<ImageSurface> images = new ArrayList<>();

        while (pos < buffer.capacity()) {
            short width = buffer.get(pos++);
            short height = buffer.get(pos++);

            // Create ImageSurface and get a pointer to the image data.
            ImageSurface surface = ImageSurface.create(Format.RGB24, width, height);
            MemorySegment imgData = surface.getData()
                    .reinterpret((long) surface.getStride() * surface.getHeight());

            for (int row = 0; row < height; row++) {
                short b = buffer.get(pos++);
                short g = buffer.get(pos++);
                short r = buffer.get(pos++);
                short i = buffer.get(pos++);

                // Convert EGA data to RGB values
                for (int x = 0; x < width; x++) {
                    int red   = (bit(r, x) * 170) + (bit(i, x) * 85);
                    int green = (bit(g, x) * 170) + (bit(i, x) * 85);
                    int blue  = (bit(b, x) * 170) + (bit(i, x) * 85);

                    // Adjust dark yellow to brown
                    // See https://en.wikipedia.org/wiki/Color_Graphics_Adapter#With_an_RGBI_monitor
                    if (red == 170 && green == 170 && blue == 0)
                        green = 85;

                    int rgb24 = (red << 16) | (green << 8) | blue;
                    imgData.setAtIndex(ValueLayout.JAVA_INT, row * 16 + x, rgb24);
                }
            }
            images.add(surface);

            pos += 3; // Skip 6 bytes (padding?)
        }
        return images;
    }

    /**
     * Determine whether the bit at position {@code pos} is 1.
     * The images are mirrored so we compensate for that
     */
    private static int bit(short int16le, int pos) {
        // Convert short to byte array with big-endian byte order
        byte[] byteArray = new byte[2];
        byteArray[1] = (byte) ((int16le >> 8) & 0xFF);
        byteArray[0] = (byte) (int16le & 0xFF);

        // Determine the byte and bit index for the specified position
        int byteIndex = pos / 8;
        int bitIndex = 7 - (pos % 8);

        // Extract the bit at the specified position
        byte targetByte = byteArray[byteIndex];
        return (targetByte >> bitIndex) & 0x01;
    }

    /**
     * Generate a 16x16 RGB image that is used to draw bars below extended
     * spikes. The image displays three vertical stripes with color #00AAAA
     * ([r,g,b] = [0,170,170]) on pixel 4, 8 and 12 (zero-indexed) with a black
     * background.
     */
    public static ImageSurface generateSpikeBarImage() {
        // Create ImageSurface and get a pointer to the image data
        ImageSurface surface = ImageSurface.create(Format.RGB24, 16, 16);
        MemorySegment imgData = surface.getData()
                .reinterpret((long) surface.getStride() * surface.getHeight());

        // Draw the vertical lines
        for (int row = 0; row < 16; row++) {
            for (int x = 0; x < 16; x++) {
                int color = (x > 0 && x % 4 == 0) ? 170 : 0;
                int rgb24 = (color << 8) | color;
                imgData.setAtIndex(ValueLayout.JAVA_INT, row * 16 + x, rgb24);
            }
        }
        return surface;
    }
}
