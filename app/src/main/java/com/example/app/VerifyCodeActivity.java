package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyCodeActivity extends AppCompatActivity {

    private EditText edtVerificationCode;
    private Button btnVerifyCode;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_code);

        mAuth = FirebaseAuth.getInstance();
        edtVerificationCode = findViewById(R.id.edtVerificationCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);

        // Recebe o ID de verificação da Intent
        verificationId = getIntent().getStringExtra("verificationId");

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
                        Toast.makeText(VerifyCodeActivity.this, "Verificação bem-sucedida!", Toast.LENGTH_SHORT).show();
                        // Navegue para a próxima atividade, por exemplo, Tela principal
                        startActivity(new Intent(VerifyCodeActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Falha na verificação: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
