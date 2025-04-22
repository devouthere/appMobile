package com.example.app.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.app.R;
import com.example.app.model.Barbeiro;
import com.example.app.view.AgendamentoActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BarbeiroAdapterTest {

    @Mock
    private ViewGroup mockParent;

    @Mock
    private Context mockContext;

    private BarbeiroAdapter adapter;
    private List<Barbeiro> listaBarbeiros;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        listaBarbeiros = new ArrayList<>();
        List<String> servicos = Arrays.asList("Corte", "Barba");
        List<String> diasDisponiveis = Arrays.asList("Segunda", "Terça");

        Barbeiro barbeiro = new Barbeiro("1", "João", "joao@email.com", "Rua 1", diasDisponiveis, servicos);
        listaBarbeiros.add(barbeiro);

        adapter = new BarbeiroAdapter(listaBarbeiros);
    }

    @Test
    public void testOnCreateViewHolder() {

        View mockView = Mockito.mock(View.class);

        BarbeiroAdapter testAdapter = new BarbeiroAdapter(listaBarbeiros) {
            @Override
            public BarbeiroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                return new BarbeiroViewHolder(mockView);
            }
        };
        BarbeiroAdapter.BarbeiroViewHolder viewHolder = testAdapter.onCreateViewHolder(mockParent, 0);

        assertNotNull(viewHolder);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(1, adapter.getItemCount());

        Barbeiro novoBarbeiro = new Barbeiro("2", "Pedro", "pedro@email.com", "Rua 2", null, null);
        listaBarbeiros.add(novoBarbeiro);

        adapter = new BarbeiroAdapter(listaBarbeiros);

        // Verifica novamente
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testUpdateData() {
        List<Barbeiro> novaLista = new ArrayList<>();
        Barbeiro novoBarbeiro = new Barbeiro("2", "Pedro", "pedro@email.com", "Rua 2", null, null);
        novaLista.add(novoBarbeiro);

        adapter.updateData(novaLista);

        assertEquals(1, adapter.getItemCount());
        assertEquals("Pedro", adapter.listaBarbeiros.get(0).getNome());
    }

    @Test
    public void testUpdateDataWithNull() {
        adapter.updateData(null);

        assertNotNull(adapter.listaBarbeiros);
        assertEquals(0, adapter.listaBarbeiros.size());
    }

    @Test
    public void testConstructorWithNull() {
        BarbeiroAdapter nullAdapter = new BarbeiroAdapter(null);
        assertNotNull(nullAdapter.listaBarbeiros);
        assertEquals(0, nullAdapter.listaBarbeiros.size());
    }

}