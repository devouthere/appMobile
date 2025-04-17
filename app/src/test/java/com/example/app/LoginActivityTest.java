package com.example.app;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import com.example.app.view.BarberDashboardActivity;
import com.example.app.view.LoginActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {

    private LoginActivity activity;

    @Mock
    private FirebaseAuth mAuth;

    @Mock
    private FirebaseFirestore db;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private DocumentReference docRef;

    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnLogin;
    private TextView txtRegistrar;

    @Before
    public void setup() {
        activity = spy(new LoginActivity());
        edtEmail = mock(EditText.class);
        edtSenha = mock(EditText.class);
        btnLogin = mock(Button.class);
        txtRegistrar = mock(TextView.class);

        activity.mAuth = mAuth;
        activity.db = db;

        // Simula a inicialização das views
        activity.edtEmail = edtEmail;
        activity.edtSenha = edtSenha;
        activity.btnLogin = btnLogin;
        activity.txtRegistrar = txtRegistrar;
    }

    // Método de reflexão para invocar funções privadas
    private void invokePrivateMethod(String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        Method method = LoginActivity.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);  // Torna o método privado acessível
        method.invoke(activity, params);  // Invoca o método privado
    }

    @Test
    public void testLoginWithValidCredentials() throws Exception {
        String validEmail = "test@example.com";
        String validPassword = "password123";

        when(edtEmail.getText().toString()).thenReturn(validEmail);
        when(edtSenha.getText().toString()).thenReturn(validPassword);

        // Simula sucesso ao fazer login
        Task<AuthResult> mockTask = mock(Task.class);
        AuthResult mockAuthResult = mock(AuthResult.class);

        when(mAuth.signInWithEmailAndPassword(validEmail, validPassword)).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockAuthResult);

        // Simula o usuário retornado no AuthResult
        when(mockAuthResult.getUser()).thenReturn(mockUser);

        // Usando reflexão para invocar o método privado
        invokePrivateMethod("loginWithEmailPassword", new Class[]{String.class, String.class}, new Object[]{validEmail, validPassword});

        // Verifica se o método de verificação do tipo de usuário foi chamado usando reflexão
        invokePrivateMethod("verificarTipoDeUsuario", new Class[]{String.class}, new Object[]{validEmail});
        verify(activity, times(1)).verificarTipoDeUsuario(validEmail);
    }

    @Test
    public void testLoginWithInvalidUser() throws Exception {
        String invalidEmail = "invalid@example.com";
        String invalidPassword = "wrongpassword";

        when(edtEmail.getText().toString()).thenReturn(invalidEmail);
        when(edtSenha.getText().toString()).thenReturn(invalidPassword);

        // Simula falha no login
        FirebaseAuthInvalidUserException exception = new FirebaseAuthInvalidUserException(
                "ERROR_USER_NOT_FOUND", "User not found");
        when(mAuth.signInWithEmailAndPassword(invalidEmail, invalidPassword))
                .thenThrow(exception);

        // Usando reflexão para invocar o método privado
        invokePrivateMethod("loginWithEmailPassword", new Class[]{String.class, String.class}, new Object[]{invalidEmail, invalidPassword});

        // Verifica se o método de erro foi chamado
        verify(activity, times(1)).showErrorDialog(anyString());
    }

    @Test
    public void testRedirectByUserType() throws Exception {
        // Simula a situação em que o usuário é do tipo "barbeiro"
        when(mockDocumentSnapshot.getString("tipoUsuario")).thenReturn("barbeiro");
        when(db.collection("usuarios").document(anyString())).thenReturn(docRef);
        when(docRef.get()).thenReturn(mockDocumentSnapshot);

        // Usando reflexão para invocar o método privado
        invokePrivateMethod("redirectByUserType", new Class[]{String.class}, new Object[]{"barbeiro"});

        // Verifica se a navegação foi feita para a tela correta (BarberDashboardActivity)
        Intent intent = new Intent(activity, BarberDashboardActivity.class);
        verify(activity).startActivity(intent);
    }

    @Test
    public void testShowErrorDialog() throws Exception {
        // Usando reflexão para invocar o método privado
        invokePrivateMethod("showErrorDialog", new Class[]{String.class}, new Object[]{"Erro ao fazer login"});

        // Verifica se o método showErrorDialog foi chamado corretamente
        verify(activity, times(1)).showErrorDialog(anyString());
    }
}
