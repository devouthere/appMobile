package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class LogarPhone extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private Button btnSendCode;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logar_phone);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnSendCode = findViewById(R.id.btnSendCode);

        btnSendCode.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(LogarPhone.this, "Digite o número de telefone", Toast.LENGTH_SHORT).show();
            } else {
                checkIfPhoneNumberExists(phoneNumber);
            }
        });
    }

    private void checkIfPhoneNumberExists(String phoneNumber) {
        db.collection("usuarios")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            sendVerificationCode(phoneNumber);
                        } else {
                            Toast.makeText(LogarPhone.this, "Número de telefone não cadastrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LogarPhone.this, "Erro ao verificar o telefone: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(LogarPhone.this, "Erro ao enviar código: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                Intent intent = new Intent(LogarPhone.this, CodeVerifyLogin.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("verificationId", verificationId);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
