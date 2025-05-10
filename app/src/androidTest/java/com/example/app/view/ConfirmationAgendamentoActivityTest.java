package com.example.app.view;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.app.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConfirmationAgendamentoActivityTest {

    @Test
    public void testAgendamentoDetails() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvServico)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDataHora)).check(matches(isDisplayed()));

            onView(withId(R.id.tvServico)).check(matches(withText("Serviço: Corte de Cabelo")));
            onView(withId(R.id.tvDataHora)).check(matches(withText("Data/Hora: 2025-06-15 10:30")));
        }
    }


    @Test
    public void testAgendamentoDetailsDisplay() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvServico)).check(matches(withText("Serviço: Corte de Cabelo")));

            onView(withId(R.id.tvDataHora)).check(matches(withText("Data/Hora: 2025-06-15 10:30")));
        }
    }

    @Test
    public void testOnBackPressed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> activity.onBackPressed());

            onView(withId(R.id.imageView)).check(matches(isDisplayed()));
        }
    }
}
