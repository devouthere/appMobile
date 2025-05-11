package com.example.app.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.controller.MainMenu;
import com.example.app.R;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.animate().alpha(0f).setDuration(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.animate().alpha(1f).setDuration(1000);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                boolean termsAccepted = sharedPref.getBoolean("terms_accepted", false);

                Intent intent;
                if (!termsAccepted) {
                    intent = new Intent(MainActivity.this, TermsConditionsActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, MainMenu.class);
                }

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}