package com.example.app.controller;

import com.example.app.model.Agendamento;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

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
}
