package io.github.jwharm.puzzlegame;

import io.github.jwharm.puzzlegame.io.ImageCache;
import io.github.jwharm.puzzlegame.io.LevelReader;
import io.github.jwharm.puzzlegame.ui.GameApplication;

public class Main {
    public static void main(String[] args) throws Exception {
        ImageCache.init("C:/DEV/PAGA1/PAGA1.012");
        LevelReader.load("C:/DEV/PAGA1/PAGA1.007");

        var app = GameApplication.create();
        app.run(args);
    }
}
