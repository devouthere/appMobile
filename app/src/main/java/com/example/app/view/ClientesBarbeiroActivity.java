package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.controller.BarbeiroAdapter;
import com.example.app.controller.MainMenu;
import com.example.app.model.Barbeiro;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClientesBarbeiroActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BarbeiroAdapter adapter;
    private List<Barbeiro> listaBarbeiros = new ArrayList<>();
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.app.R.layout.activity_clientes_barbeiro);

        Toolbar toolbar = findViewById(com.example.app.R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(com.example.app.R.id.drawer_layout);
        navigationView = findViewById(com.example.app.R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, com.example.app.R.string.openDrawer, com.example.app.R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == com.example.app.R.id.nav_home) {
                Toast.makeText(this, "Agendamentos selecionado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ClientesBarbeiroActivity.this, DashboardClientActivity.class));
                finish();
            } else if (id == com.example.app.R.id.nav_profile) {
                Toast.makeText(this, "Já está na tela de barbeiros disponíveis", Toast.LENGTH_SHORT).show();
            } else if (id == com.example.app.R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ClientesBarbeiroActivity.this, MainMenu.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        recyclerView = findViewById(R.id.recyclerViewBarbeiros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BarbeiroAdapter(listaBarbeiros);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        carregarBarbeiros();
    }

    private void carregarBarbeiros() {
        db.collection("barbeiro")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String barbeiroId = doc.getId();
                        String nome = doc.getString("nome");
                        String email = doc.getString("email");
                        String endereco = doc.getString("endereco");

                        List<String> diasDisponiveis = (List<String>) doc.get("diasDisponiveis");
                        if (diasDisponiveis == null) {
                            diasDisponiveis = new ArrayList<>();
                        }

                        List<String> servicos = (List<String>) doc.get("servicos");
                        if (servicos == null) {
                            servicos = new ArrayList<>();
                        }

                        Barbeiro barbeiro = new Barbeiro(barbeiroId, nome, email, endereco, diasDisponiveis, servicos);
                        listaBarbeiros.add(barbeiro);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao carregar barbeiros", e));
    }
}