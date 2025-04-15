package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.app.R;
import com.example.app.controller.MainMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BarberDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_CREATE_STORE = 1;
    private static final String TAG = "BarberDashboard";

    private TextView txtNome, tvUserEmail, tvUserPhone, txtEnderecoLoja, txtServicos, txtDiasFuncionamento;
    private Button btnLoja;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_dashboard);

        try {
            initViews();
            setupFirebase();
            setupNavigationDrawer();
            loadUserData();
            checkIfBarberDataExists();
        } catch (Exception e) {
            Log.e(TAG, "Erro no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao inicializar a tela", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        txtNome = findViewById(R.id.txtNome);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        txtEnderecoLoja = findViewById(R.id.txtEnderecoLoja);
        txtServicos = findViewById(R.id.txtServicos);
        txtDiasFuncionamento = findViewById(R.id.txtDiasFuncionamento);
        btnLoja = findViewById(R.id.btnLoja);

        btnLoja.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                db.collection("barbeiro").document(user.getUid())
                        .get()
                        .addOnSuccessListener(document -> {
                            Intent intent = new Intent(BarberDashboardActivity.this, CreateStoreActivity.class);
                            if (document.exists()) {
                                intent.putExtra("editing", true);
                            }
                            startActivityForResult(intent, REQUEST_CODE_CREATE_STORE);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Erro ao verificar loja existente", e);
                            Toast.makeText(BarberDashboardActivity.this, "Erro ao verificar dados", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setupNavigationDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                tvUserEmail.setText(user.getEmail());
                tvUserPhone.setText("Não informado");
            } else if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                tvUserPhone.setText(user.getPhoneNumber());
                tvUserEmail.setText("Não informado");
            }

            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                txtNome.setText(document.getString("nome"));
                                if (document.contains("telefone")) {
                                    tvUserPhone.setText(document.getString("telefone"));
                                }
                            }
                        } else {
                            Log.e(TAG, "Erro ao carregar dados do usuário", task.getException());
                        }
                    });
        }
    }

    private void checkIfBarberDataExists() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("barbeiro").document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            runOnUiThread(() -> {
                                if (document != null && document.exists()) {
                                    displayBarberData(document);
                                    btnLoja.setText("ALTERAR LOJA");
                                } else {
                                    btnLoja.setText("CRIAR LOJA");
                                }
                            });
                        }
                    });
        }
    }

    private void displayBarberData(DocumentSnapshot document) {
        try {
            txtEnderecoLoja.setText(document.getString("endereco"));

            List<String> servicos = (List<String>) document.get("servicos");
            txtServicos.setText(servicos != null && !servicos.isEmpty() ?
                    TextUtils.join(", ", servicos) : "Nenhum serviço cadastrado");

            List<String> diasDisponiveis = (List<String>) document.get("diasDisponiveis");
            txtDiasFuncionamento.setText(diasDisponiveis != null && !diasDisponiveis.isEmpty() ?
                    TextUtils.join(", ", diasDisponiveis) : "Nenhum dia cadastrado");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao exibir dados", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_STORE && resultCode == RESULT_OK) {
            checkIfBarberDataExists();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfBarberDataExists();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, BarberDashboardActivity.class));
            finish();
        } else if (id == R.id.nav_clientes) {
            startActivity(new Intent(this, BarberClientsActivity.class));
            finish();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, MainMenu.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}