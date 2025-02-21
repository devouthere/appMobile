package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainMenu extends AppCompatActivity {

    Button signinemail, signinphone, signup;
    ImageView bgimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define o layout primeiro
        setContentView(R.layout.activity_main_menu);

        // Inicializa os botões
        Button btnEmail = findViewById(R.id.SignWithEmail);
        Button btnTelefone = findViewById(R.id.SignWithTelefone);
        Button btnLogin = findViewById(R.id.SignWithLogin);

        // Carregar animações
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.anim_main_menu);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_main_menu);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.anim_main_menu);

        // Aplicar animações nos botões
        btnEmail.startAnimation(pulse);
        btnTelefone.startAnimation(fadeIn);
        btnLogin.startAnimation(slideUp);

        // Adicionar clique no botão de e-mail
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, ChooseOption.class);
            startActivity(intent);
        });

        // Ativa o EdgeToEdge e ajusta os paddings conforme os insets do sistema
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
