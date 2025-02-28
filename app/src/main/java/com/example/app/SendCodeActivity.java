package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendCodeActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private Button btnSendCode;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_code);

        mAuth = FirebaseAuth.getInstance();
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnSendCode = findViewById(R.id.btnSendCode);

        btnSendCode.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, "Digite um número de telefone", Toast.LENGTH_SHORT).show();
                return;
            }

            phoneNumber = formatPhoneNumber(phoneNumber);

            if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Número de telefone inválido!", Toast.LENGTH_SHORT).show();
                return;
            }

            sendVerificationCode(phoneNumber);
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches() && phoneNumber.startsWith("+");
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (!phoneNumber.startsWith("+55")) {
            phoneNumber = "+55" + phoneNumber.replaceAll("[^\\d]", "");
        }
        return phoneNumber;
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(com.google.firebase.auth.PhoneAuthCredential credential) {
                        navigateToVerification(verificationId);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(SendCodeActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String id, PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = id;
                        resendingToken = token;
                        Toast.makeText(SendCodeActivity.this, "Código enviado!", Toast.LENGTH_SHORT).show();
                        navigateToVerification(verificationId);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void navigateToVerification(String verificationId) {
        Intent intent = new Intent(SendCodeActivity.this, VerifyCodeActivity.class);
        intent.putExtra("verificationId", verificationId);
        startActivity(intent);
        finish();
    }
}
