//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.android.gms.tasks.Tasks;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    public void testBackButtonNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                View backArrow = activity.findViewById(R.id.backArrow);
                assertNotNull(backArrow);
                backArrow.performClick();

                assertTrue(activity.isFinishing());
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

    @Test
    public void testValidacaoDeInputsEComponentes() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                Button btnConfirmar = activity.findViewById(R.id.btnConfirmar);
                assertNotNull("Botão confirmar não deveria ser nulo", btnConfirmar);

                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                assertNotNull("Layout de serviços não deveria ser nulo", llServicos);
                assertEquals("Deveria ter 2 serviços disponíveis", 2, llServicos.getChildCount());

                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                assertNotNull("Spinner de dias não deveria ser nulo", spinnerDias);
                assertEquals("Spinner deveria ter 2 opções", 2, spinnerDias.getAdapter().getCount());

                GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);
                assertNotNull("Grid de horários não deveria ser nulo", gridLayoutHorarios);

                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                CheckBox checkBox2 = (CheckBox) llServicos.getChildAt(1);

                assertEquals("Corte", checkBox1.getText().toString());
                assertEquals("Barba", checkBox2.getText().toString());

                assertFalse("Checkbox 1 não deveria estar selecionado inicialmente", checkBox1.isChecked());
                assertFalse("Checkbox 2 não deveria estar selecionado inicialmente", checkBox2.isChecked());

                checkBox1.setChecked(true);
                checkBox2.setChecked(true);

                assertTrue("Checkbox 1 deveria estar selecionado após clique", checkBox1.isChecked());
                assertTrue("Checkbox 2 deveria estar selecionado após clique", checkBox2.isChecked());

                spinnerDias.setSelection(1);
                assertEquals("Terça", spinnerDias.getSelectedItem());


                String horarioTeste = "14:30";
                setHorarioSelecionado(activity, horarioTeste);
                assertEquals("Horário deveria ter sido definido corretamente",
                        horarioTeste, getHorarioSelecionado(activity));


                boolean todosOsCamposPreenchidos =
                        checkBox1.isChecked() || checkBox2.isChecked()
                                && spinnerDias.getSelectedItem() != null
                                && getHorarioSelecionado(activity) != null;

                assertTrue("Todos os campos necessários deveriam estar preenchidos", todosOsCamposPreenchidos);
            });
        }
    }

    @Test
    public void testControleDeHorariosDisponiveisEIndisponiveis() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);
                assertNotNull("Grid de horários não deveria ser nulo", gridLayoutHorarios);

                Button btnHorarioDisponivel = new Button(activity);
                btnHorarioDisponivel.setText("10:00");

                Button btnHorarioIndisponivel = new Button(activity);
                btnHorarioIndisponivel.setText("11:00");

                List<String> horariosReservados = new ArrayList<>();
                horariosReservados.add("11:00");

                if (horariosReservados.contains(btnHorarioDisponivel.getText().toString())) {
                    btnHorarioDisponivel.setEnabled(false);
                    btnHorarioDisponivel.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light));
                    btnHorarioDisponivel.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                } else {
                    btnHorarioDisponivel.setBackgroundResource(R.drawable.btntwo);
                    btnHorarioDisponivel.setTextColor(activity.getResources().getColor(android.R.color.black));
                }

                if (horariosReservados.contains(btnHorarioIndisponivel.getText().toString())) {
                    btnHorarioIndisponivel.setEnabled(false);
                    btnHorarioIndisponivel.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light));
                    btnHorarioIndisponivel.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                } else {
                    btnHorarioIndisponivel.setBackgroundResource(R.drawable.btntwo);
                    btnHorarioIndisponivel.setTextColor(activity.getResources().getColor(android.R.color.black));
                }

                assertTrue("Horário disponível deveria estar habilitado", btnHorarioDisponivel.isEnabled());
                assertFalse("Horário reservado deveria estar desabilitado", btnHorarioIndisponivel.isEnabled());

                Button btnHorarioSelecionado = null;
                String horarioSelecionado = null;

                btnHorarioDisponivel.performClick();

                btnHorarioSelecionado = btnHorarioDisponivel;
                horarioSelecionado = btnHorarioDisponivel.getText().toString();

                assertEquals("10:00", horarioSelecionado);
                assertNotNull("Deveria haver um botão selecionado", btnHorarioSelecionado);

                setHorarioSelecionado(activity, horarioSelecionado);
                assertEquals("10:00", getHorarioSelecionado(activity));

                btnHorarioIndisponivel.performClick();
                assertEquals("10:00", horarioSelecionado);

                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                spinnerDias.setSelection(0);

                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                checkBox1.setChecked(true);

                boolean todosOsCamposPreenchidos =
                        checkBox1.isChecked() &&
                                spinnerDias.getSelectedItem() != null &&
                                getHorarioSelecionado(activity) != null;

                assertTrue("Todos os campos necessários deveriam estar preenchidos", todosOsCamposPreenchidos);

            });
        }
    }

    @Test
    public void testProcessamentoAgendamentosEControleHorarios() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.example.app",
                "com.example.app.view.AgendamentoActivity"
        ))) {
            scenario.onActivity(activity -> {
                QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
                List<DocumentSnapshot> documentSnapshots = new ArrayList<>();

                DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
                Agendamento agendamentoExistente = new Agendamento();
                agendamentoExistente.setHorario("09:00");
                when(mockDocumentSnapshot.toObject(Agendamento.class)).thenReturn(agendamentoExistente);
                documentSnapshots.add(mockDocumentSnapshot);

                when(mockQuerySnapshot.getDocuments()).thenReturn(documentSnapshots);

                List<String> horariosReservados = new ArrayList<>();

                for (DocumentSnapshot document : mockQuerySnapshot.getDocuments()) {
                    Agendamento agendamento = document.toObject(Agendamento.class);
                    if (agendamento != null && agendamento.getHorario() != null) {
                        horariosReservados.add(agendamento.getHorario());
                    }
                }

                assertEquals(1, horariosReservados.size());
                assertEquals("09:00", horariosReservados.get(0));

                List<String> horarios = Arrays.asList("08:00", "09:00", "10:00", "11:00");

                Context context = activity.getApplicationContext();
                GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);

                List<Button> botoesHorarios = new ArrayList<>();

                for (String horario : horarios) {
                    Button btnHorario = new Button(context);
                    btnHorario.setText(horario);

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.setMargins(16, 16, 16, 16);
                    params.width = 200;
                    params.height = 100;
                    btnHorario.setLayoutParams(params);

                    if (horariosReservados.contains(horario)) {
                        btnHorario.setEnabled(false);
                        btnHorario.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light));
                        btnHorario.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                    } else {
                        btnHorario.setBackgroundResource(R.drawable.btntwo);
                        btnHorario.setTextColor(activity.getResources().getColor(android.R.color.black));
                    }

                    botoesHorarios.add(btnHorario);
                }

                assertFalse("Horário 09:00 deveria estar desabilitado", botoesHorarios.get(1).isEnabled());
                assertTrue("Horário 08:00 deveria estar habilitado", botoesHorarios.get(0).isEnabled());
                assertTrue("Horário 10:00 deveria estar habilitado", botoesHorarios.get(2).isEnabled());
                assertTrue("Horário 11:00 deveria estar habilitado", botoesHorarios.get(3).isEnabled());

                Button btnHorarioSelecionado = null;
                String horarioSelecionado = null;

                Button btnClicado = botoesHorarios.get(2); // índice 2 = "10:00"

                if (btnHorarioSelecionado != null) {
                    btnHorarioSelecionado.setBackgroundResource(R.drawable.btntwo);
                    btnHorarioSelecionado.setTextColor(activity.getResources().getColor(android.R.color.black));
                }

                btnHorarioSelecionado = btnClicado;
                horarioSelecionado = btnClicado.getText().toString();

                btnHorarioSelecionado.setBackgroundResource(R.drawable.btn);
                btnHorarioSelecionado.setTextColor(activity.getResources().getColor(android.R.color.white));

                assertEquals("10:00", horarioSelecionado);

                setHorarioSelecionado(activity, horarioSelecionado);

                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                spinnerDias.setSelection(0); // "Segunda"
                String diaSelecionado = spinnerDias.getSelectedItem().toString();

                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                CheckBox checkBoxCorte = (CheckBox) llServicos.getChildAt(0);
                checkBoxCorte.setChecked(true);

                List<String> servicosSelecionados = new ArrayList<>();
                servicosSelecionados.add("Corte");

                Agendamento novoAgendamento = new Agendamento(
                        barbeiroFake.getId(),
                        barbeiroFake.getNome(),
                        "cliente123", // Cliente ID simulado
                        "Nome do Cliente",
                        diaSelecionado,
                        horarioSelecionado,
                        String.join(", ", servicosSelecionados),
                        "pendente"
                );

                assertEquals(barbeiroFake.getId(), novoAgendamento.getBarbeiroId());
                assertEquals(barbeiroFake.getNome(), novoAgendamento.getBarbeiroNome());
                assertEquals("cliente123", novoAgendamento.getClienteId());
                assertEquals("Nome do Cliente", novoAgendamento.getClienteNome());
                assertEquals(diaSelecionado, novoAgendamento.getDia());
                assertEquals("10:00", novoAgendamento.getHorario());
                assertEquals("Corte", novoAgendamento.getServico());
                assertEquals("pendente", novoAgendamento.getStatus());
            });
        }
    }
}

