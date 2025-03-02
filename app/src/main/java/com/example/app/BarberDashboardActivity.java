package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class BarberDashboardActivity extends AppCompatActivity {

    private TextView txtNome, txtEndereco, tvUserId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnCriarLoja;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_dashboard);

        // Referência para os TextViews
        txtNome = findViewById(R.id.txtNome);
        txtEndereco = findViewById(R.id.txtEndereco);
        tvUserId = findViewById(R.id.tvUserId);

        // Inicializando Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCriarLoja = findViewById(R.id.btnCriarLoja);

        // Configuração da Toolbar e do menu hambúrguer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Adiciona o botão de abrir menu no Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Clique nos itens do menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Início selecionado", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Clientes selecionado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BarberDashboardActivity.this, ClientesBarbeiroActivity.class));
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(BarberDashboardActivity.this, MainMenu.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Navegar para a tela de criar loja
        btnCriarLoja.setOnClickListener(v -> {
            Intent intent = new Intent(BarberDashboardActivity.this, CreateStoreActivity.class);
            startActivity(intent);
        });

        // Buscar UID e exibir na tela
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            tvUserId.setText(uid);
            Log.d("FirebaseAuth", "UID do usuário: " + uid);
            carregarDadosBarbeiro(uid);
        } else {
            tvUserId.setText("Nenhum usuário logado");
            Log.d("FirebaseAuth", "Nenhum usuário logado.");
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarDadosBarbeiro(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    String endereco = document.getString("endereco");

                    txtNome.setText(nome);
                    txtEndereco.setText(endereco);

                    Log.d("Firestore", "Nome: " + nome + ", Endereço: " + endereco);
                } else {
                    Log.w("Firestore", "Documento não encontrado para UID: " + userId);
                    Toast.makeText(this, "Dados do barbeiro não encontrados!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firestore", "Erro ao buscar documento", task.getException());
                Toast.makeText(this, "Erro ao buscar dados: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
