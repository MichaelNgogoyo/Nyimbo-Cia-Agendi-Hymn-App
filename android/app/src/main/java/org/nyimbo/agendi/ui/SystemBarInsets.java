package org.nyimbo.agendi.ui;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/** Keeps Android's status and navigation bars visible while content respects their insets. */
final class SystemBarInsets {
    private SystemBarInsets() {}

    static void apply(AppCompatActivity activity, View root) {
        apply(activity, root, root);
    }

    static void apply(AppCompatActivity activity, View insetSource, View... insetTargets) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 29) activity.getWindow().setNavigationBarContrastEnforced(false);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(activity.getWindow(), insetSource);
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        final int[][] basePadding = new int[insetTargets.length][4];
        for (int i = 0; i < insetTargets.length; i++) {
            View target = insetTargets[i];
            basePadding[i][0] = target.getPaddingLeft();
            basePadding[i][1] = target.getPaddingTop();
            basePadding[i][2] = target.getPaddingRight();
            basePadding[i][3] = target.getPaddingBottom();
        }
        ViewCompat.setOnApplyWindowInsetsListener(insetSource, (view, windowInsets) -> {
            androidx.core.graphics.Insets insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            for (int i = 0; i < insetTargets.length; i++) {
                View target = insetTargets[i];
                target.setPadding(basePadding[i][0] + insets.left,
                    basePadding[i][1] + insets.top,
                    basePadding[i][2] + insets.right,
                    basePadding[i][3] + insets.bottom);
            }
            // These views now account for the complete system-bar insets themselves.
            return WindowInsetsCompat.CONSUMED;
        });
        insetSource.post(() -> ViewCompat.requestApplyInsets(insetSource));
    }
}
