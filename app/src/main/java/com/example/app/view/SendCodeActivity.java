package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.controller.ChooseOption;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class SendCodeActivity extends AppCompatActivity {

    private EditText edtPhoneNumber, edtNome, edtEmail, edtEndereco;
    private Button btnSendCode;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationId;
    private boolean isBarbeiro; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_code);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SendCodeActivity.this, ChooseOption.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEndereco = findViewById(R.id.edtEndereco);
        btnSendCode = findViewById(R.id.btnSendCode);


        Intent intent = getIntent();
        isBarbeiro = intent.getBooleanExtra("isBarbeiro", false);


        if (isBarbeiro) {
            edtEndereco.setVisibility(View.VISIBLE);
        }

        btnSendCode.setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String endereco = edtEndereco.getText().toString().trim();


            if (TextUtils.isEmpty(nome)) {
                Toast.makeText(this, "Digite seu nome", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Digite um e-mail", Toast.LENGTH_SHORT).show();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "E-mail inválido. Verifique o formato", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, "Digite um número de telefone", Toast.LENGTH_SHORT).show();
                return;
            } else if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Número de telefone inválido. Verifique o formato", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isBarbeiro && TextUtils.isEmpty(endereco)) {
                Toast.makeText(this, "Digite seu endereço", Toast.LENGTH_SHORT).show();
                return;
            }

            phoneNumber = formatPhoneNumber(phoneNumber);
            checkIfEmailOrPhoneExists(email, phoneNumber);
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

    private void checkIfEmailOrPhoneExists(String email, String phoneNumber) {
        db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(SendCodeActivity.this, "E-mail já cadastrado!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.collection("usuarios")
                                .whereEqualTo("phoneNumber", phoneNumber)
                                .get()
                                .addOnCompleteListener(phoneTask -> {
                                    if (phoneTask.isSuccessful()) {
                                        if (!phoneTask.getResult().isEmpty()) {
                                            Toast.makeText(SendCodeActivity.this, "Número de telefone já cadastrado!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        sendVerificationCode(phoneNumber);
                                    } else {
                                        Toast.makeText(SendCodeActivity.this, "Erro ao verificar telefone: " + phoneTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(SendCodeActivity.this, "Erro ao verificar e-mail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        Log.d("VerifyCodeActivity", "Received phone number: " + phoneNumber);

        Intent intent = new Intent(SendCodeActivity.this, VerifyCodeActivity.class);
        intent.putExtra("verificationId", verificationId);
        intent.putExtra("nome", edtNome.getText().toString().trim());
        intent.putExtra("email", edtEmail.getText().toString().trim());
        intent.putExtra("phoneNumber", phoneNumber);

        if (isBarbeiro) {
            intent.putExtra("endereco", edtEndereco.getText().toString().trim());
        }

        intent.putExtra("isBarbeiro", isBarbeiro);
        startActivity(intent);
        finish();
    }
}
