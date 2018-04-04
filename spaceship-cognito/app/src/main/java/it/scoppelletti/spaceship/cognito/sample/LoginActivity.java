package it.scoppelletti.spaceship.cognito.sample;

import android.content.Intent;
import it.scoppelletti.spaceship.cognito.app.LoginActivityBase;

public final class LoginActivity extends LoginActivityBase {

    public LoginActivity() {
    }

    @Override
    protected void onLoginSucceeded() {
        Intent intent;

        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
