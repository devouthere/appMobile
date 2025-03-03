package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DashboardClientActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAgendamentos;
    private AgendamentoAdapter agendamentoAdapter;
    private FirebaseFirestore db;
    private String clienteId;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_client);

        // Inicializando os componentes
        recyclerViewAgendamentos = findViewById(R.id.recyclerViewAgendamentos);
        recyclerViewAgendamentos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializando Firebase
        db = FirebaseFirestore.getInstance();
        clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Carregar agendamentos
        carregarAgendamentos();

        // Configuração da Toolbar e DrawerLayout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Configura o botão do menu hamburguer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configuração do listener do menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Início selecionado", Toast.LENGTH_SHORT).show();
                // Retornar para a tela de Dashboard
                startActivity(new Intent(DashboardClientActivity.this, DashboardClientActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(DashboardClientActivity.this, ClientesBarbeiroActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashboardClientActivity.this, MainMenu.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void carregarAgendamentos() {
        db.collection("agendamentos")
                .whereEqualTo("clienteId", clienteId)  // Filtra os agendamentos do cliente logado
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Agendamento> agendamentos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Recuperar os dados do agendamento
                            Agendamento agendamento = document.toObject(Agendamento.class);
                            agendamentos.add(agendamento);
                        }

                        // Configura o adapter com os dados
                        agendamentoAdapter = new AgendamentoAdapter(agendamentos);
                        recyclerViewAgendamentos.setAdapter(agendamentoAdapter);
                    } else {
                        Toast.makeText(DashboardClientActivity.this, "Erro ao carregar agendamentos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
