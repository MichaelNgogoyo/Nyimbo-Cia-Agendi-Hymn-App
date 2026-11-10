package org.nyimbo.agendi.ui;

import androidx.fragment.app.Fragment;

public final class EventsFragmentFactory {
    private EventsFragmentFactory() {}
    public static Fragment create() { return new EventsFragment(); }
}
