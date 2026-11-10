package org.nyimbo.agendi.ui;

import androidx.fragment.app.Fragment;

/** The first Play release is a local hymnal; event screens are not shipped. */
public final class EventsFragmentFactory {
    private EventsFragmentFactory() {}
    public static Fragment create() { return HymnsFragment.newInstance(false); }
}
