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

import io.github.jwharm.puzzlegame.ui.Messages;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class to extract game files from the zip archive.
 */
public class ArchiveReader {

    // The first four bytes of a ZIP archive: PK♥♦ or "PK\3\4"
    private static final byte[] ZIP_HEADER = {0x50, 0x4b, 0x03, 0x04};

    // Find the position of the zip header in the InputStream
    private static long findZipHeaderStart(InputStream in) throws IOException {
        int bufferSize = 4;
        byte[] buffer = new byte[bufferSize];
        long position = 0;

        while (in.read(buffer, 0, bufferSize) != -1) {
            if (Arrays.equals(buffer, ZIP_HEADER))
                return position;
            position++;
            in.reset();
            in.skip(position);
        }

        return -1;
    }

    // Extract the requested files from a self-extracting zip archive.
    private Map<String, byte[]> extractSfx(InputStream sfx, List<String> files)
            throws IOException {

        sfx.mark(Integer.MAX_VALUE);
        long start = findZipHeaderStart(sfx);
        sfx.reset();
        if (start == -1)
            throw new IOException("Cannot find zip header in archive");
        sfx.skip(start);
        return extract(sfx, files);
    }

    // Extract the requested files from the stream (a zip archive).
    private Map<String, byte[]> extract(InputStream stream, List<String> files)
            throws IOException {

        // Process all files in the zip archive
        Map<String, byte[]> map = new HashMap<>();
        try (var zip = new ZipInputStream(stream)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();

                // Strip directories from file names
                if (name.contains("/"))
                    name = name.substring(name.indexOf("/") + 1);

                // Check if this is a requested file
                if (files.contains(name)) {

                    // Extract the file, and store it in the map
                    byte[] content = extract(zip);
                    map.put(name, content);
                }
                zip.closeEntry();
            }
        }
        return map;
    }

    // Extract the contents of a single file.
    private byte[] extract(ZipInputStream in) throws IOException {
        int bufferSize = 4096;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = in.read(buffer, 0, bufferSize)) != -1)
            bos.write(buffer, 0, read);
        return bos.toByteArray();
    }

    /**
     * Extract the game data (levels and images) from the Paganitzu shareware
     * archive.
     */
    public Map<String, byte[]> extractGameAssets(String filename)
            throws IOException {

        try (var stream = new FileInputStream(filename)) {

            /*
             * The zip file contains a number of files, mostly used for
             * installing the game. The actual game data is in "PAGA.1".
             */
            var map = extract(stream, List.of("PAGA.1", "PAGA1.007", "PAGA1.012"));

            if (map.isEmpty())
                throw new IOException(Messages.INVALID_FILE);

            /*
             * If the zip contains the extracted game files, we're already done.
             */
            if (map.containsKey("PAGA1.007") && map.containsKey("PAGA1.012"))
                return map;

            /*
             * PAGA.1 is a self-extracting archive, that contains the actual
             * game files. Extract PAGA1.007 (levels) and PAGA1.012 (images).
             */
            return extractSfx(
                    new ByteArrayInputStream(map.get("PAGA.1")),
                    List.of("PAGA1.007", "PAGA1.012"));
        }
    }
}