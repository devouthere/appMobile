package com.example.app.view;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Classe base para activities com barra de status transparente
 * Esta classe implementa o gerenciamento de WindowInsets para evitar que
 * elementos de UI fiquem cobertos pela barra de status transparente
 */
public class TransparentStatusBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
    }

    protected void setupTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Para Android 11+ (API 30+)
                WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
                WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
                controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Para Android 6.0+ (API 23+)
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }

        // Aguarda até que a view seja anexada
        // e então configura os WindowInsets
        getWindow().getDecorView().post(this::setupWindowInsets);
    }

    private void setupWindowInsets() {
        // Configura o listener de insets para lidar com os paddings
        final View content = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            return insets; // Retorna os insets sem modificar
        });
    }

    // Para dispositivos mais antigos ou se precisar ajustar a cor dos ícones da barra de status
    protected void setLightStatusBar(boolean isLight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isLight) {
                // Ícones escuros para fundos claros
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                // Ícones claros para fundos escuros
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    /**
     * Método de ajuda para aplicar padding superior no primeiro elemento da hierarquia
     * de views para evitar sobreposição com a barra de status
     * @param view A view que precisa de padding superior
     */
    protected void applyTopInsetPadding(View view) {
        if (view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                int originalPadding = v.getPaddingTop();

                // Só adiciona o padding se não tiver sido adicionado antes
                if (v.getTag() == null || !((Boolean)v.getTag())) {
                    v.setPadding(
                            v.getPaddingLeft(),
                            originalPadding + statusBarHeight,
                            v.getPaddingRight(),
                            v.getPaddingBottom()
                    );
                    v.setTag(true); // Marca que o padding já foi adicionado
                }

                return insets;
            });
        }
    }
}