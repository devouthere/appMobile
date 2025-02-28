package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateStoreActivity extends AppCompatActivity {

    private EditText edtEndereco;
    private CheckBox chkCorte, chkBarba, chkSobrancelha;
    private CheckBox chkDia1, chkDia2, chkDia3, chkDia4, chkDia5, chkDia6, chkDia7;
    private Button btnSalvar;

    private DatabaseReference storeRef;
    private String barberUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        // Inicializa Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        barberUid = user.getUid();
        storeRef = FirebaseDatabase.getInstance().getReference("lojas").child(barberUid);

        // Referências dos componentes
        edtEndereco = findViewById(R.id.edtEndereco);
        chkCorte = findViewById(R.id.chkCorte);
        chkBarba = findViewById(R.id.chkBarba);
        chkSobrancelha = findViewById(R.id.chkSobrancelha);

        chkDia1 = findViewById(R.id.chkDia1);
        chkDia2 = findViewById(R.id.chkDia2);
        chkDia3 = findViewById(R.id.chkDia3);
        chkDia4 = findViewById(R.id.chkDia4);
        chkDia5 = findViewById(R.id.chkDia5);
        chkDia6 = findViewById(R.id.chkDia6);
        chkDia7 = findViewById(R.id.chkDia7);

        btnSalvar = findViewById(R.id.btnSalvar);

        // Ação do botão de salvar
        btnSalvar.setOnClickListener(v -> salvarLoja());
    }

    private void salvarLoja() {
        String endereco = edtEndereco.getText().toString().trim();
        if (endereco.isEmpty()) {
            Toast.makeText(this, "Digite um endereço!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Coletando serviços escolhidos
        Map<String, Boolean> servicos = new HashMap<>();
        servicos.put("Corte de Cabelo", chkCorte.isChecked());
        servicos.put("Barba", chkBarba.isChecked());
        servicos.put("Sobrancelha", chkSobrancelha.isChecked());

        // Coletando dias de trabalho escolhidos
        Map<String, Boolean> diasTrabalho = new HashMap<>();
        diasTrabalho.put("01", chkDia1.isChecked());
        diasTrabalho.put("02", chkDia2.isChecked());
        diasTrabalho.put("03", chkDia3.isChecked());
        diasTrabalho.put("04", chkDia4.isChecked());
        diasTrabalho.put("05", chkDia5.isChecked());
        diasTrabalho.put("06", chkDia6.isChecked());
        diasTrabalho.put("07", chkDia7.isChecked());

        // Criando o objeto Loja
        Map<String, Object> lojaData = new HashMap<>();
        lojaData.put("endereco", endereco);
        lojaData.put("servicos", servicos);
        lojaData.put("diasTrabalho", diasTrabalho);

        // Salvando no Firebase
        storeRef.setValue(lojaData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreateStoreActivity.this, "Loja salva com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CreateStoreActivity.this, "Erro ao salvar!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
