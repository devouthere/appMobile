//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AgendamentoActivityTest {

    private Barbeiro barbeiroFake;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private Query mockQuery;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private DocumentReference mockDocumentReference;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        barbeiroFake = new Barbeiro();
        barbeiroFake.setId("barbeiro123");
        barbeiroFake.setNome("João Barbeiro");
        barbeiroFake.setServicos(Arrays.asList("Corte", "Barba"));
        barbeiroFake.setDiasDisponiveis(Arrays.asList("Segunda", "Terça"));

        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("cliente123");
        when(mockDb.collection("agendamentos")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.whereIn(anyString(), any(List.class))).thenReturn(mockQuery);
        when(mockDocumentReference.getId()).thenReturn("agendamento123");
    }

    private String getHorarioSelecionado(AgendamentoActivity activity) {
        try {
            java.lang.reflect.Field field = AgendamentoActivity.class.getDeclaredField("horarioSelecionado");
            field.setAccessible(true);
            return (String) field.get(activity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setHorarioSelecionado(AgendamentoActivity activity, String horario) {
        try {
            java.lang.reflect.Field field = AgendamentoActivity.class.getDeclaredField("horarioSelecionado");
            field.setAccessible(true);
            field.set(activity, horario);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOnCreateActivityWithValidBarbeiroIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

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

    @Test
    public void testServicoCheckboxesCreatedCorrectly() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                assertEquals(2, llServicos.getChildCount());

                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                CheckBox checkBox2 = (CheckBox) llServicos.getChildAt(1);

                assertEquals("Corte", checkBox1.getText().toString());
                assertEquals("Barba", checkBox2.getText().toString());
            });
        }
    }

    @Test
    public void testSpinnerDiasPreenchidoCorretamente() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                assertEquals(2, spinnerDias.getAdapter().getCount());
                assertEquals("Segunda", spinnerDias.getAdapter().getItem(0));
                assertEquals("Terça", spinnerDias.getAdapter().getItem(1));
            });
        }
    }

    @Test
    public void testSelecaoDeHorario() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                activity.findViewById(R.id.gridLayoutHorarios).post(() -> {
                    GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);
                    Button primeiroBtn = (Button) gridLayoutHorarios.getChildAt(0);

                    if (primeiroBtn != null) {
                        primeiroBtn.setEnabled(true);
                        primeiroBtn.performClick();

                        assertEquals(View.VISIBLE, primeiroBtn.getVisibility());
                        assertEquals(primeiroBtn.getText().toString(), getHorarioSelecionado(activity));
                    } else {
                        fail("Botão de horário não encontrado");
                    }
                });
            });
        }
    }

    @Test
    public void testSelecaoDiaNoSpinner() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                Spinner spinner = activity.findViewById(R.id.spinnerDias);
                spinner.setSelection(1);
                assertEquals("Terça", spinner.getSelectedItem());
            });
        }
    }

    @Test
    public void testSalvarAgendamentoSemServicosSelecionados() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                Button btnConfirmar = activity.findViewById(R.id.btnConfirmar);
                btnConfirmar.performClick();
                verify(mockCollectionReference, times(0)).add(any());
            });
        }
    }

    @Test
    public void testSalvarAgendamentoSemHorarioSelecionado() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                ((CheckBox) llServicos.getChildAt(0)).setChecked(true);
                setHorarioSelecionado(activity, null);

                Button btnConfirmar = activity.findViewById(R.id.btnConfirmar);
                btnConfirmar.performClick();
                verify(mockCollectionReference, times(0)).add(any());
            });
        }
    }
}
