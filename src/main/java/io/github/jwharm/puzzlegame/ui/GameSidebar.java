package io.github.jwharm.puzzlegame.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.types.Types;
import org.gnome.adw.ActionRow;
import org.gnome.adw.PreferencesGroup;
import org.gnome.glib.Type;
import org.gnome.gobject.GObject;
import org.gnome.gtk.Box;
import org.gnome.gtk.Orientation;

import java.lang.foreign.MemorySegment;

public class GameSidebar extends Box {

    private static final Type gtype = Types.register(GameSidebar.class);

    public static Type getType() {
        return gtype;
    }

    public GameSidebar(MemorySegment address) {
        super(address);
    }

    public static GameSidebar create() {
        GameSidebar instance = GObject.newInstance(getType(),
                "orientation", Orientation.VERTICAL,
                "width-request", 200);
        return instance;
    }

    @InstanceInit
    public void init() {
        PreferencesGroup prefGroup = new PreferencesGroup();
        var actionRow = ActionRow.builder()
                .setVisible(true)
                .setTitle("Row 1")
                .setActivatable(false)
                //.setChild(Image.fromFile(""))
                .build();
        prefGroup.add(actionRow);
        super.append(prefGroup);
    }
}
