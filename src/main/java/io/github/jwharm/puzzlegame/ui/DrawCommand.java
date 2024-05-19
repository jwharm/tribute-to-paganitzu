package io.github.jwharm.puzzlegame.ui;

import org.freedesktop.cairo.Context;

@FunctionalInterface
public interface DrawCommand {
    void draw(Context cr);
}
