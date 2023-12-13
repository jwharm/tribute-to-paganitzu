package io.github.jwharm.puzzlegame;

import io.github.jwharm.puzzlegame.ui.GameApplication;

public class Main {
    public static void main(String[] args) {
        var app = GameApplication.create();
        app.run(args);
    }
}
