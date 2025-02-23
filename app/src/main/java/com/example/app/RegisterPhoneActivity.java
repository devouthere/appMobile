package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterPhoneActivity extends AppCompatActivity {

    private EditText edtPhoneNumber, edtVerificationCode;
    private Button btnSendCode, btnVerifyCode;

    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referências dos componentes
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtVerificationCode = findViewById(R.id.edtVerificationCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);

        // Botão para enviar o código de verificação via SMS
        btnSendCode.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(RegisterPhoneActivity.this, "Digite um número de telefone", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVerificationCode(phoneNumber);
        });

        // Botão para verificar o código digitado
        btnVerifyCode.setOnClickListener(v -> {
            String code = edtVerificationCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(RegisterPhoneActivity.this, "Digite o código de verificação", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });
    }

    // Método para enviar o código de verificação via SMS
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Autenticação automática (caso o telefone consiga detectar o código)
                                signInWithCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(RegisterPhoneActivity.this, "Falha ao enviar código: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                verificationId = id;
                                resendingToken = token;
                                Toast.makeText(RegisterPhoneActivity.this, "Código enviado!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Método para verificar o código manualmente
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    // Método para autenticar com o Firebase usando o código
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterPhoneActivity.this, "Verificação concluída!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterPhoneActivity.this, MainMenu.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterPhoneActivity.this, "Erro ao verificar código", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
