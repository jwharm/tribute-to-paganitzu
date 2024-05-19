package io.github.jwharm.puzzlegame;

import io.github.jwharm.puzzlegame.ui.GameApplication;
import org.gnome.gio.Resource;

public class Main {
    public static void main(String[] args) throws Exception {
        Resource resource = Resource.load("src/main/resources/game.gresource");
        resource.resourcesRegister();

        var app = GameApplication.create();
        app.run(args);
    }
}
