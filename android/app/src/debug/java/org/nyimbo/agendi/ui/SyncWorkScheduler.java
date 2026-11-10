package org.nyimbo.agendi.ui;

import android.content.Context;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import org.nyimbo.agendi.sync.SyncWorker;
import java.util.concurrent.TimeUnit;

public final class SyncWorkScheduler {
    private SyncWorkScheduler() {}
    public static void schedule(Context context) {
        Constraints network = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodic = new PeriodicWorkRequest.Builder(SyncWorker.class, 6, TimeUnit.HOURS).setConstraints(network).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("nyimbo-periodic-sync", ExistingPeriodicWorkPolicy.KEEP, periodic);
        OneTimeWorkRequest initial = new OneTimeWorkRequest.Builder(SyncWorker.class).setConstraints(network).build();
        WorkManager.getInstance(context).enqueueUniqueWork("nyimbo-initial-sync", ExistingWorkPolicy.KEEP, initial);
    }
}
