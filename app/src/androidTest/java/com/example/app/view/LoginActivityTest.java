//./gradlew connectedDebugAndroidTest
//./gradlew createDebugCoverageReport
//./gradlew testDebugUnitTestCoverage


package com.example.app.view;

import com.example.app.R;
import com.example.app.controller.MainMenu;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testBackArrowClick_shouldNavigateToMainMenu() {
        // Interage com o botão de voltar        onView(withId(R.id.backArrow)).perform(click());

        intended(hasComponent(MainMenu.class.getName()));
    }

    @Test
    public void testViews_onCreate_shouldBeDisplayed() {
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
    }

    @Test
    public void testFirebaseRelatedComponentsAreDisplayed() {
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.edtEmail)).check(matches(isDisplayed()));
    }
}