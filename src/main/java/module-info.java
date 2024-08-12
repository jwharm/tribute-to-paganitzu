module io.github.jwharm.tribute {
    requires org.gnome.adw;
    exports io.github.jwharm.tribute;
    exports io.github.jwharm.tribute.ui to org.gnome.glib,org.gnome.gobject,org.gnome.gdk,org.gnome.gtk;
}
