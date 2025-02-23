package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    // Referências aos campos da tela
    private EditText edtNome, edtEmail, edtSenha, edtEndereco;
    private Button btnRegistrar;

    // Instâncias do Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        // Inicializar a instância do Firestore e Firebase Authentication
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obter referências dos campos de entrada
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtEndereco = findViewById(R.id.edtEndereco);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Verificar se é barbeiro
        boolean isBarbeiro = getIntent().getBooleanExtra("isBarbeiro", false);

        // Ajustar visibilidade do campo de endereço
        if (isBarbeiro) {
            edtEndereco.setVisibility(View.VISIBLE); // Exibir para barbeiro
        } else {
            edtEndereco.setVisibility(View.GONE); // Ocultar para cliente
        }

        // Configurar ação do botão de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter os dados dos campos
                String nome = edtNome.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String senha = edtSenha.getText().toString().trim();
                String endereco = edtEndereco.getText().toString().trim();

                // Verificar se os campos obrigatórios estão preenchidos
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Registrar no Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Usuário criado com sucesso no Firebase Authentication
                                // Agora, criar um objeto User para salvar no Firestore
                                String tipoUsuario = isBarbeiro ? "barbeiro" : "cliente";
                                User user = new User(nome, email, senha, isBarbeiro ? endereco : null, tipoUsuario);

                                // Salvar o usuário no Firestore
                                db.collection("usuarios") // Nome da coleção
                                        .add(user) // Adiciona o usuário
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(RegisterActivity.this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
                                            finish(); // Fecha a atividade após sucesso
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(RegisterActivity.this, "Erro ao registrar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Caso haja erro ao criar o usuário no Firebase Authentication
                                Toast.makeText(RegisterActivity.this, "Erro ao registrar usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
