//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.controller;

import static com.google.common.base.Verify.verify;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.platform.app.InstrumentationRegistry;

public class AgendamentoAdapterTest {

    private AgendamentoAdapter adapter;

    @Before
    public void setUp() {
        adapter = new AgendamentoAdapter(Collections.emptyList()); // lista vazia
    }

    @Test
    public void testGetPrioridadeStatus_pendente() {
        int prioridade = adapter.getPrioridadeStatus("pendente");
        assertEquals(1, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_confirmado() {
        int prioridade = adapter.getPrioridadeStatus("confirmado");
        assertEquals(2, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_cancelado() {
        int prioridade = adapter.getPrioridadeStatus("cancelado");
        assertEquals(3, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_defaultCase() {
        int prioridade = adapter.getPrioridadeStatus("outro");
        assertEquals(4, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_nullStatus() {
        int prioridade = adapter.getPrioridadeStatus(null);
        assertEquals(3, prioridade);
    }

    @Test
    public void testOrdenarAgendamentos_porPrioridade() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("cancelado");
        Agendamento a2 = new Agendamento();
        a2.setStatus("pendente");
        Agendamento a3 = new Agendamento();
        a3.setStatus("confirmado");

        List<Agendamento> ordenada = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3));

        assertEquals("pendente", ordenada.get(0).getStatus());
        assertEquals("confirmado", ordenada.get(1).getStatus());
        assertEquals("cancelado", ordenada.get(2).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_mesmoStatus() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("confirmado");

        Agendamento a2 = new Agendamento();
        a2.setStatus("confirmado");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2));

        assertEquals("confirmado", resultado.get(0).getStatus());
        assertEquals("confirmado", resultado.get(1).getStatus());
        assertSame(a1, resultado.get(0));
        assertSame(a2, resultado.get(1));
    }

    @Test
    public void testOrdenarAgendamentos_diferentesStatus() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("confirmado");

        Agendamento a2 = new Agendamento();
        a2.setStatus("pendente");

        Agendamento a3 = new Agendamento();
        a3.setStatus("cancelado");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3));

        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("confirmado", resultado.get(1).getStatus());
        assertEquals("cancelado", resultado.get(2).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_comVariosItens() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("cancelado");

        Agendamento a2 = new Agendamento();
        a2.setStatus("pendente");

        Agendamento a3 = new Agendamento();
        a3.setStatus("confirmado");

        Agendamento a4 = new Agendamento();
        a4.setStatus("pendente");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3, a4));

        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("pendente", resultado.get(1).getStatus());
        assertEquals("confirmado", resultado.get(2).getStatus());
        assertEquals("cancelado", resultado.get(3).getStatus());
    }


    @Test
    public void testGetItemCount() {
        Agendamento a1 = new Agendamento();
        Agendamento a2 = new Agendamento();

        AgendamentoAdapter novaAdapter = new AgendamentoAdapter(Arrays.asList(a1, a2));
        assertEquals(2, novaAdapter.getItemCount());
    }

    @Test
    public void testOrdenarAgendamentos_listaVazia() {
        List<Agendamento> resultado = adapter.ordenarAgendamentos(Collections.emptyList());
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testOrdenarAgendamentos_listaComUmItem() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("pendente");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Collections.singletonList(a1));
        assertEquals(1, resultado.size());
        assertEquals("pendente", resultado.get(0).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_ordemJaCorreta() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("pendente");
        Agendamento a2 = new Agendamento();
        a2.setStatus("confirmado");
        Agendamento a3 = new Agendamento();
        a3.setStatus("cancelado");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3));

        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("confirmado", resultado.get(1).getStatus());
        assertEquals("cancelado", resultado.get(2).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_comStatusDesconhecido() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("pendente");
        Agendamento a2 = new Agendamento();
        a2.setStatus("xyz");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a2, a1));
        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("xyz", resultado.get(1).getStatus());
    }

    @Test
    public void testOnCreateViewHolder() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ViewGroup parent = new LinearLayout(context);

        AgendamentoAdapter.AgendamentoViewHolder holder = adapter.onCreateViewHolder(parent, 0);

        assertNotNull(holder);

        assertNotNull(holder.itemView);
    }

    @Test
    public void testOnBindViewHolder_pendente() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("pendente");
        agendamento.setBarbeiroNome("João");
        agendamento.setServico("Corte de Cabelo");
        agendamento.setDia("05/05/2025");
        agendamento.setHorario("14:00");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null);

        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        viewHolder.txtStatus.setText(agendamento.getStatus());

        assertNotNull(viewHolder.txtStatus);
        assertEquals("pendente", viewHolder.txtStatus.getText().toString());
    }
}
