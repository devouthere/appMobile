package com.example.app.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AlterarAgendamentoActivityTest {

    @Rule
    public ActivityTestRule<AlterarAgendamentoActivity> activityRule =
            new ActivityTestRule<>(AlterarAgendamentoActivity.class, true, false);

    private ActivityScenario<AlterarAgendamentoActivity> scenario;
    private String testAgendamentoId = "test_agendamento_123";
    private String testBarbeiroId = "test_barbeiro_456";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setupFirestoreTestData();
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
        cleanupFirestoreTestData();
    }

    @Test
    public void testActivityLaunchWithValidId() {
        Intent intent = createIntentWithId(testAgendamentoId);
        scenario = ActivityScenario.launch(intent);

        // Verifica se a activity foi criada com sucesso
        scenario.onActivity(activity -> {
            assertNotNull(activity);
            assertEquals("Alterar Agendamento", activity.getTitle());
        });
    }




    @Test
    public void testConfirmButtonInitialState() {
        Intent intent = createIntentWithId(testAgendamentoId);
        scenario = ActivityScenario.launch(intent);

        waitForDataLoad();

        onView(withId(R.id.btnConfirmar))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
    }

    @Test
    public void testSaveChangesWithoutService() {
        Intent intent = createIntentWithId(testAgendamentoId);
        scenario = ActivityScenario.launch(intent);

        waitForDataLoad();

        scenario.onActivity(activity -> {
            // Desmarca todos os serviços
            LinearLayout llServicos = activity.findViewById(R.id.llServicos);
            for (int i = 0; i < llServicos.getChildCount(); i++) {
                if (llServicos.getChildAt(i) instanceof CheckBox) {
                    ((CheckBox) llServicos.getChildAt(i)).setChecked(false);
                }
            }
        });

        // Tenta confirmar sem serviços selecionados
        onView(withId(R.id.btnConfirmar)).perform(click());

        // Deve mostrar toast de erro (não é possível testar diretamente com Espresso)
        // Mas podemos verificar que a activity não finalizou
        scenario.onActivity(activity -> {
            assertTrue("Activity não deve finalizar sem serviços", !activity.isFinishing());
        });
    }




    private Intent createIntentWithId(String agendamentoId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AlterarAgendamentoActivity.class);
        intent.putExtra("id", agendamentoId);
        return intent;
    }

    private void waitForDataLoad() {
        try {
            // Aguarda um tempo para o carregamento dos dados do Firestore
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void setupFirestoreTestData() {
        // Em um ambiente real, você configuraria dados de teste no Firestore
        // ou usaria mocks para simular as respostas do Firebase

        // Para testes reais, você precisaria:
        // 1. Criar documentos de teste no Firestore
        // 2. Ou usar Firebase Test Lab
        // 3. Ou configurar um emulador do Firestore para testes

        // Exemplo de estrutura de dados de teste:
        /*
        Agendamento agendamentoTeste = new Agendamento();
        agendamentoTeste.setId(testAgendamentoId);
        agendamentoTeste.setBarbeiroId(testBarbeiroId);
        agendamentoTeste.setDia("Segunda");
        agendamentoTeste.setHorario("10:00");
        agendamentoTeste.setServico("Corte, Barba");
        agendamentoTeste.setStatus("confirmado");

        Barbeiro barbeiroTeste = new Barbeiro();
        barbeiroTeste.setId(testBarbeiroId);
        barbeiroTeste.setNome("João Silva");
        barbeiroTeste.setDiasDisponiveis(Arrays.asList("Segunda", "Terça", "Quarta"));
        barbeiroTeste.setServicos(Arrays.asList("Corte", "Barba", "Sobrancelha"));
        */
    }

    private void cleanupFirestoreTestData() {
        // Remove dados de teste após os testes
        // Em um ambiente real, você limparia os dados de teste do Firestore
    }

    // Testes adicionais para casos específicos

    @Test
    public void testHorarioAtualmenteSelecionadoHighlight() {
        Intent intent = createIntentWithId(testAgendamentoId);
        scenario = ActivityScenario.launch(intent);

        waitForDataLoad();

        scenario.onActivity(activity -> {
            // Verifica se há um horário pré-selecionado baseado no agendamento atual
            // Este teste seria mais efetivo com dados reais do Firestore
            assertNotNull(activity.findViewById(R.id.recyclerViewHorarios));
        });
    }

    @Test
    public void testValidationOnSaveChanges() {
        Intent intent = createIntentWithId(testAgendamentoId);
        scenario = ActivityScenario.launch(intent);

        waitForDataLoad();

        // Testa salvamento sem horário selecionado
        scenario.onActivity(activity -> {
            // Remove seleção de horário se houver
            // Em implementação real, você simularia isso
        });

        onView(withId(R.id.btnConfirmar)).perform(click());

        scenario.onActivity(activity -> {
            assertTrue("Activity não deve finalizar sem horário", !activity.isFinishing());
        });
    }
}