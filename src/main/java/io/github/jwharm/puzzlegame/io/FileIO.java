package io.github.jwharm.puzzlegame.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileIO {

    public static byte[] readFile(String name) {
        try (InputStream in = new FileInputStream(name)) {
            return in.readAllBytes();
        } catch (IOException e) {
            System.out.println("Error reading " + name + ": " + e.getMessage());
            return new byte[0];
        }
    }
}
