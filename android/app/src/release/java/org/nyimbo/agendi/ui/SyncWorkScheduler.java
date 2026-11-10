package org.nyimbo.agendi.ui;

import android.content.Context;

/** No backend sync is included in the offline first release. */
public final class SyncWorkScheduler {
    private SyncWorkScheduler() {}
    public static void schedule(Context context) { }
}
