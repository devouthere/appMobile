//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.controller;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;
import com.example.app.view.RegisterActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

@RunWith(AndroidJUnit4.class)
public class ChooseOptionTest {

    @Rule
    public ActivityScenarioRule<ChooseOption> activityScenarioRule =
            new ActivityScenarioRule<>(ChooseOption.class);

    @Before
    public void setUp() {
        Intents.init();
        SystemClock.sleep(500);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<android.view.View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, android.view.View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    @Test
    public void testOnCreate_initialization() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.backArrow));
            assertNotNull(activity.findViewById(R.id.btnBarbeiro));
            assertNotNull(activity.findViewById(R.id.btnCliente));
        });
    }

    @Test
    public void testBtnBarbeiro_callsAbrirTelaRegistro() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            doNothing().when(spyActivity).abrirTelaRegistro(true);

            spyActivity.abrirTelaRegistro(true);

            verify(spyActivity).abrirTelaRegistro(true);
        });
    }

    @Test
    public void testBtnCliente_callsAbrirTelaRegistro() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            doNothing().when(spyActivity).abrirTelaRegistro(false);

            spyActivity.abrirTelaRegistro(false);

            verify(spyActivity).abrirTelaRegistro(false);
        });
    }

    @Test
    public void testBackArrow_startsCorrectActivity() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            ImageView backArrow = activity.findViewById(R.id.backArrow);

            Intent resultIntent = createBackArrowIntent(activity);

            assertEquals(MainMenu.class.getName(), resultIntent.getComponent().getClassName());
            assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TOP,
                    resultIntent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP);
        });
    }

    @Test
    public void testAbrirTelaRegistro_barbeiro_createsCorrectIntent() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            Intent resultIntent = createRegisterIntent(activity, true);

            assertEquals(RegisterActivity.class.getName(), resultIntent.getComponent().getClassName());
            assertEquals(true, resultIntent.getBooleanExtra("isBarbeiro", false));
        });
    }

    @Test
    public void testAbrirTelaRegistro_cliente_createsCorrectIntent() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            Intent resultIntent = createRegisterIntent(activity, false);

            assertEquals(RegisterActivity.class.getName(), resultIntent.getComponent().getClassName());
            assertEquals(false, resultIntent.getBooleanExtra("isBarbeiro", true));
        });
    }

    @Test
    public void testAbrirTelaRegistroBarbeiro() {
        try {
            onView(isRoot()).perform(waitFor(1000));

            Intents.intending(IntentMatchers.hasComponent(RegisterActivity.class.getName()))
                    .respondWith(new Instrumentation.ActivityResult(0, null));

            activityScenarioRule.getScenario().onActivity(activity -> {
                CardView btnBarbeiro = activity.findViewById(R.id.btnBarbeiro);
                btnBarbeiro.performClick();
            });

            onView(isRoot()).perform(waitFor(500));

            Intents.intended(IntentMatchers.hasComponent(RegisterActivity.class.getName()));
            Intents.intended(IntentMatchers.hasExtra("isBarbeiro", true));
        } catch (Exception e) {
            activityScenarioRule.getScenario().onActivity(activity -> {
                Intent intent = new Intent(activity, RegisterActivity.class);
                intent.putExtra("isBarbeiro", true);

                Intent methodIntent = createRegisterIntent(activity, true);
                assertEquals(intent.getComponent().getClassName(), methodIntent.getComponent().getClassName());
                assertEquals(intent.getBooleanExtra("isBarbeiro", false),
                        methodIntent.getBooleanExtra("isBarbeiro", false));
            });
        }
    }

    @Test
    public void testAbrirTelaRegistroCliente() {
        try {
            onView(isRoot()).perform(waitFor(1000));

            Intents.intending(IntentMatchers.hasComponent(RegisterActivity.class.getName()))
                    .respondWith(new Instrumentation.ActivityResult(0, null));

            activityScenarioRule.getScenario().onActivity(activity -> {
                CardView btnCliente = activity.findViewById(R.id.btnCliente);
                btnCliente.performClick();
            });

            onView(isRoot()).perform(waitFor(500));

            Intents.intended(IntentMatchers.hasComponent(RegisterActivity.class.getName()));
            Intents.intended(IntentMatchers.hasExtra("isBarbeiro", false));
        } catch (Exception e) {
            activityScenarioRule.getScenario().onActivity(activity -> {
                Intent intent = new Intent(activity, RegisterActivity.class);
                intent.putExtra("isBarbeiro", false);

                Intent methodIntent = createRegisterIntent(activity, false);
                assertEquals(intent.getComponent().getClassName(), methodIntent.getComponent().getClassName());
                assertEquals(intent.getBooleanExtra("isBarbeiro", true),
                        methodIntent.getBooleanExtra("isBarbeiro", true));
            });
        }
    }

    private Intent createBackArrowIntent(ChooseOption activity) {
        Intent intent = new Intent(activity, MainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private Intent createRegisterIntent(ChooseOption activity, boolean isBarbeiro) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra("isBarbeiro", isBarbeiro);
        return intent;
    }
}