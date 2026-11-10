package org.nyimbo.agendi.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.materialswitch.MaterialSwitch;
import org.nyimbo.agendi.BuildConfig;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.sync.EventReminderScheduler;

public class SettingsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        var preferences = requireContext().getSharedPreferences("preferences", 0);

        RadioGroup appearance = root.findViewById(R.id.appearance_options);
        String mode = preferences.getString("theme_mode", "system");
        appearance.check("light".equals(mode) ? R.id.theme_light
            : "dark".equals(mode) ? R.id.theme_dark : R.id.theme_system);
        appearance.setOnCheckedChangeListener((group, checkedId) -> {
            String selected = checkedId == R.id.theme_light ? "light"
                : checkedId == R.id.theme_dark ? "dark" : "system";
            preferences.edit().putString("theme_mode", selected).apply();
            AppCompatDelegate.setDefaultNightMode(MainActivity.modeToNightMode(selected));
        });

        MaterialSwitch notifications = root.findViewById(R.id.notifications);
        if (BuildConfig.OFFLINE_RELEASE) {
            root.findViewById(R.id.reminders_divider).setVisibility(View.GONE);
            root.findViewById(R.id.reminders_title).setVisibility(View.GONE);
            notifications.setVisibility(View.GONE);
            return root;
        }
        boolean granted = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        notifications.setChecked(preferences.getBoolean("eventNotifications", false) && granted);
        notifications.setOnCheckedChangeListener((button, checked) -> {
            if (checked && Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notifications.setChecked(false);
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 108);
                return;
            }
            preferences.edit().putBoolean("eventNotifications", checked).apply();
            if (checked) EventReminderScheduler.scheduleBookmarked(requireContext());
            PushSubscriptionHelper.update(requireContext(), checked);
        });
        return root;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 108 && getContext() != null) {
            boolean allowed = results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED;
            requireContext().getSharedPreferences("preferences", 0).edit().putBoolean("eventNotifications", allowed).apply();
            if (allowed) {
                MaterialSwitch toggle = getView() == null ? null : getView().findViewById(R.id.notifications);
                if (toggle != null) toggle.setChecked(true);
                EventReminderScheduler.scheduleBookmarked(requireContext());
            }
            Toast.makeText(requireContext(), allowed ? "Event reminders are on." : "Notifications remain off.", Toast.LENGTH_SHORT).show();
        }
    }
}
