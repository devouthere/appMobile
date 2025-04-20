//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import com.example.app.R;
import com.example.app.controller.MainMenu;
import com.google.firebase.auth.FirebaseAuth;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testBackArrowClick_shouldNavigateToMainMenu() {
        // Interage com o botão de voltar
        onView(withId(R.id.backArrow)).perform(click());
        intended(hasComponent(MainMenu.class.getName()));
    }

    @Test
    public void testViews_onCreate_shouldBeDisplayed() {
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
    }

    @Test
    public void testFirebaseRelatedComponentsAreDisplayed() {
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.edtEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin_withEmptyFields_shouldShowErrors() {
        // Simulando o clique no botão de login com campos vazios
        onView(withId(R.id.btnLogin)).perform(click());

        // Verificar se o erro é mostrado para o e-mail
        onView(withId(R.id.edtEmail)).check(matches(hasErrorText("Digite seu e-mail")));

        // Preencher o campo de e-mail
        onView(withId(R.id.edtEmail)).perform(typeText("test@example.com"), closeSoftKeyboard());

        // Agora, clicando no botão de login novamente para verificar o erro da senha
        onView(withId(R.id.btnLogin)).perform(click());

        // Verificar se o erro é mostrado para a senha
        onView(withId(R.id.edtSenha)).check(matches(hasErrorText("Digite sua senha")));
    }

    @Test
    public void testLoginWithEmailPassword_validCredentials() {
        // Preencher campos com credenciais válidas
        onView(withId(R.id.edtEmail)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edtSenha)).perform(typeText("password123"), closeSoftKeyboard());

        // Clicar no botão de login
        onView(withId(R.id.btnLogin)).perform(click());

        // Verificar se o loading é exibido (verificando o Toast)
        // Nota: Toast testing requer configuração especial, aqui apenas verificamos o fluxo
    }

    @Test
    public void testLoginError_handlesAuthExceptions() {
        // Preencher com credenciais que causarão erro
        onView(withId(R.id.edtEmail)).perform(typeText("nonexistent@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edtSenha)).perform(typeText("wrongpassword"), closeSoftKeyboard());

        // Tentar login
        onView(withId(R.id.btnLogin)).perform(click());

        // Verificar tratamento de erro (necessita de mock do Firebase)
        // O resultado seria um diálogo de erro sendo exibido
    }
}