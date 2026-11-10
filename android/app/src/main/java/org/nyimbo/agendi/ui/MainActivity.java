package org.nyimbo.agendi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import org.nyimbo.agendi.BuildConfig;
import org.nyimbo.agendi.R;

public class MainActivity extends AppCompatActivity {
    private static final String THEME_MODE = "theme_mode";
    private final SparseArray<Fragment> fragments = new SparseArray<>();
    private int activeId = R.id.nav_home;
    private DrawerLayout drawer;
    private NavigationView navigation;
    private ImageButton searchButton;

    @Override protected void onCreate(Bundle saved) {
        SplashScreen.installSplashScreen(this);
        applySavedTheme();
        super.onCreate(saved);
        setContentView(R.layout.activity_main);
        SystemBarInsets.apply(this, findViewById(R.id.drawer_layout),
            findViewById(R.id.main_content), findViewById(R.id.navigation_drawer));

        int[] navIds = {R.id.nav_home, R.id.nav_events, R.id.nav_favorites, R.id.nav_settings};
        for (int navId : navIds) {
            Fragment restored = getSupportFragmentManager().findFragmentByTag("nav-" + navId);
            if (restored != null) fragments.put(navId, restored);
        }
        drawer = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.navigation_drawer);
        // Keep the original two-tone vector artwork; NavigationView's default tint
        // was tinting both the icon and its circular background into blank discs.
        navigation.setItemIconTintList(null);
        navigation.getMenu().findItem(R.id.nav_events).setVisible(!BuildConfig.OFFLINE_RELEASE);
        findViewById(R.id.menu_button).setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> toggleHomeSearch());
        navigation.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_about) {
                showAbout();
            } else if (id == R.id.nav_share) {
                shareApp();
            } else {
                show(id);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        activeId = saved == null ? R.id.nav_home : saved.getInt("active", R.id.nav_home);
        navigation.setCheckedItem(activeId);
        show(activeId);
        getSupportFragmentManager().executePendingTransactions();
        updateSearchButton();
        if (!BuildConfig.OFFLINE_RELEASE) SyncWorkScheduler.schedule(this);
    }

    private void applySavedTheme() {
        var prefs = getSharedPreferences("preferences", MODE_PRIVATE);
        String mode = prefs.getString(THEME_MODE, null);
        if (mode == null) {
            // Migrate the earlier Light/Dark setting once; fresh installs follow Android's theme.
            if (prefs.contains("dark")) {
                mode = prefs.getBoolean("dark", false) ? "dark" : "light";
                prefs.edit().putString(THEME_MODE, mode).remove("dark").apply();
            } else {
                mode = "system";
            }
        }
        AppCompatDelegate.setDefaultNightMode(modeToNightMode(mode));
    }

    static int modeToNightMode(String mode) {
        if ("dark".equals(mode)) return AppCompatDelegate.MODE_NIGHT_YES;
        if ("light".equals(mode)) return AppCompatDelegate.MODE_NIGHT_NO;
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.privacy_policy_label, (dialog, which) -> showPrivacyPolicy())
            .show();
    }

    private void showPrivacyPolicy() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.privacy_policy_label)
            .setMessage(R.string.privacy_policy_text)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }

    private void shareApp() {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
        startActivity(Intent.createChooser(send, getString(R.string.share_chooser)));
    }

    private Fragment fragmentFor(int id) {
        Fragment existing = fragments.get(id);
        if (existing != null) return existing;
        Fragment next;
        if (id == R.id.nav_events) next = EventsFragmentFactory.create();
        else if (id == R.id.nav_favorites) next = HymnsFragment.newInstance(true);
        else if (id == R.id.nav_settings) next = new SettingsFragment();
        else next = HymnsFragment.newInstance(false);
        fragments.put(id, next);
        return next;
    }

    private void show(int id) {
        if (BuildConfig.OFFLINE_RELEASE && id == R.id.nav_events) id = R.id.nav_home;
        if (id != R.id.nav_home && id != R.id.nav_events && id != R.id.nav_favorites && id != R.id.nav_settings) {
            id = R.id.nav_home;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        for (int navId : new int[]{R.id.nav_home, R.id.nav_events, R.id.nav_favorites, R.id.nav_settings}) {
            Fragment fragment = fragments.get(navId);
            if (fragment != null && fragment.isAdded() && navId != id) tx.hide(fragment);
        }
        Fragment target = fragmentFor(id);
        if (target.isAdded()) tx.show(target);
        else tx.add(R.id.content_container, target, "nav-" + id);
        tx.commit();
        activeId = id;
        if (navigation != null) navigation.setCheckedItem(id);
        if (searchButton != null) searchButton.post(this::updateSearchButton);
    }

    private void toggleHomeSearch() {
        if (activeId != R.id.nav_home) {
            show(R.id.nav_home);
            getSupportFragmentManager().executePendingTransactions();
        }
        Fragment home = fragments.get(R.id.nav_home);
        if (home instanceof HymnsFragment) ((HymnsFragment)home).toggleSearch();
        updateSearchButton();
    }

    private void updateSearchButton() {
        if (searchButton == null) return;
        Fragment home = fragments.get(R.id.nav_home);
        boolean searchVisible = activeId == R.id.nav_home
            && home instanceof HymnsFragment && ((HymnsFragment)home).isSearchVisible();
        searchButton.setImageResource(searchVisible ? R.drawable.ic_close_plain : R.drawable.ic_search_plain);
        searchButton.setContentDescription(getString(searchVisible ? R.string.close_search : R.string.search_songs));
    }

    public void openReader(long id) {
        startActivity(new Intent(this, ReaderActivity.class).putExtra(ReaderActivity.EXTRA_HYMN_ID, id));
    }

    public void openHymns() {
        if (navigation != null) navigation.setCheckedItem(R.id.nav_home);
        show(R.id.nav_home);
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        out.putInt("active", activeId);
        super.onSaveInstanceState(out);
    }

    @Override public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (activeId != R.id.nav_home) {
            show(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}
