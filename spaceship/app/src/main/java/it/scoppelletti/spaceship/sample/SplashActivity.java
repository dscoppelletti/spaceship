package it.scoppelletti.spaceship.sample;

import java.util.concurrent.TimeUnit;
import android.content.Intent;
import android.support.annotation.NonNull;
import io.reactivex.Completable;
import it.scoppelletti.spaceship.app.SplashActivityBase;

public final class SplashActivity extends SplashActivityBase {

    public SplashActivity() {
    }

    @NonNull
    @Override
    protected Completable createTask() {
        return Completable.complete()
            .delay(5, TimeUnit.SECONDS);
    }

    @Override
    protected void onSucceeded() {
        Intent intent;

        intent = new Intent(getApplicationContext(), DrawerActivity.class);
        startActivity(intent);
        finish();
    }
}
