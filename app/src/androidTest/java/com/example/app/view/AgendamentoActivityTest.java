package com.example.app.view;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.app.R;
import com.example.app.model.Barbeiro;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AgendamentoActivityTest {

    @Test
    public void testOnCreateActivityWithValidBarbeiroIntent() {
        // Criar um barbeiro de teste
        Barbeiro barbeiroFake = new Barbeiro();
        barbeiroFake.setId("barbeiro123");
        barbeiroFake.setNome("João Barbeiro");
        barbeiroFake.setServicos(Arrays.asList("Corte", "Barba"));
        barbeiroFake.setDiasDisponiveis(Arrays.asList("Segunda", "Terça"));

        // Criar um intent com o barbeiro como extra
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        // Lançar a Activity com o Intent
        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.llServicos));
                assertNotNull(activity.findViewById(R.id.spinnerDias));
                assertNotNull(activity.findViewById(R.id.btnConfirmar));
                assertNotNull(activity.findViewById(R.id.gridLayoutHorarios));
            });
        }
    }
}
