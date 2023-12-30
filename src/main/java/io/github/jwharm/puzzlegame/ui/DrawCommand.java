package io.github.jwharm.puzzlegame.ui;

import org.freedesktop.cairo.Context;

public interface DrawCommand {
    void draw(Context cr);
}
