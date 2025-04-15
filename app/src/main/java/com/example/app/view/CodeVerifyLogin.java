package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class CodeVerifyLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String mVerificationId;
    private EditText otpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verify_login);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(CodeVerifyLogin.this, SendCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        otpEditText = findViewById(R.id.otpEditText);


        mVerificationId = getIntent().getStringExtra("verificationId");


        findViewById(R.id.verifyCodeButton).setOnClickListener(v -> {
            String code = otpEditText.getText().toString().trim();
            if (!code.isEmpty() && mVerificationId != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(CodeVerifyLogin.this, "Por favor, insira o código", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            checkUserExists(user.getUid());
                        }
                    } else {
                        Toast.makeText(CodeVerifyLogin.this, "Erro no login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserExists(String userId) {
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tipoUsuario = documentSnapshot.getString("tipoUsuario");

                        if (tipoUsuario != null) {

                            if ("barbeiro".equals(tipoUsuario)) {
                                startActivity(new Intent(CodeVerifyLogin.this, BarberDashboardActivity.class));
                            } else if ("cliente".equals(tipoUsuario)) {
                                startActivity(new Intent(CodeVerifyLogin.this, ClientesBarbeiroActivity.class));
                            } else {
                                Toast.makeText(CodeVerifyLogin.this, "Tipo de usuário inválido", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CodeVerifyLogin.this, "Tipo de usuário não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(CodeVerifyLogin.this, "Número não cadastrado. Registre-se primeiro.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CodeVerifyLogin.this, "Erro ao verificar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
