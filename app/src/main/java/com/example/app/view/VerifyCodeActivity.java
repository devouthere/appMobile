package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyCodeActivity extends AppCompatActivity {

    private EditText edtVerificationCode;
    private Button btnVerifyCode;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String verificationId;

    private String nome, email, phoneNumber, endereco;
    private boolean isBarbeiro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtVerificationCode = findViewById(R.id.edtVerificationCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);

        verificationId = getIntent().getStringExtra("verificationId");
        nome = getIntent().getStringExtra("nome");
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        isBarbeiro = getIntent().getBooleanExtra("isBarbeiro", false);
        endereco = isBarbeiro ? getIntent().getStringExtra("endereco") : "";

        btnVerifyCode.setOnClickListener(v -> {
            String code = edtVerificationCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Digite o código", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        checkAndRegisterUser(userId);
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Falha na verificação: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndRegisterUser(String userId) {
        db.collection("usuarios").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            registerUser(userId);
                        } else {
                            redirectToDashboard(userId);
                        }
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Erro ao verificar usuário!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser(String userId) {
        boolean isBarbeiro = getIntent().getBooleanExtra("isBarbeiro", false);
        String tipoUsuario = isBarbeiro ? "barbeiro" : "cliente";
        String senha = "";
        Log.d("VerifiyCodeActivity", "verificando valores" + phoneNumber);
        User newUser = new User(
                nome,
                email,
                senha,
                phoneNumber,
                endereco,
                tipoUsuario
        );

        db.collection("usuarios").document(userId).set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("VerifyCodeActivity", "User object: " + newUser.toString());
                    Toast.makeText(VerifyCodeActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    redirectToDashboard(userId);
                })
                .addOnFailureListener(e -> Toast.makeText(VerifyCodeActivity.this, "Erro ao cadastrar usuário: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void redirectToDashboard(String userId) {
        db.collection("usuarios").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String tipoUsuario = document.getString("tipoUsuario");
                            if ("barbeiro".equals(tipoUsuario)) {
                                startActivity(new Intent(VerifyCodeActivity.this, BarberDashboardActivity.class));
                            } else {
                                startActivity(new Intent(VerifyCodeActivity.this, ClientesBarbeiroActivity.class));
                            }
                            finish();
                        }
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Erro ao verificar usuário!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
