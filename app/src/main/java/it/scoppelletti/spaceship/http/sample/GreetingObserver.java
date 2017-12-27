package it.scoppelletti.spaceship.http.sample;

import android.support.annotation.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class GreetingObserver extends DisposableSingleObserver<String> {

    GreetingObserver() {
    }

    @Override
    public void onSuccess(@NonNull String resp) {
        myLogger.debug("Response: {}", resp);
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error("Service failed.", ex);
    }
}
