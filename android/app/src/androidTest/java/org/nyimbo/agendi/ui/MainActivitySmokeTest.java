package org.nyimbo.agendi.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyimbo.agendi.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivitySmokeTest {
    @Test public void bottomNavigationOpensHymnVersionTabs() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
            onView(withId(R.id.nav_hymns)).perform(click());
            onView(withId(R.id.old_version)).check(matches(isDisplayed()));
            onView(withId(R.id.new_version)).check(matches(isDisplayed()));
        }
    }
}
