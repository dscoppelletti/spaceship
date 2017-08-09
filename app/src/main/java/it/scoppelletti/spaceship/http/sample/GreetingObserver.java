package it.scoppelletti.spaceship.http.sample;

import android.support.annotation.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.rx.SingleObserverFactory;

@Slf4j
final class GreetingObserver extends DisposableSingleObserver<String> {

    private GreetingObserver() {
    }

    @NonNull
    static SingleObserverFactory<String> newFactory() {
        return new SingleObserverFactory<String>() {

            @NonNull
            @Override
            public DisposableSingleObserver<String> create() {
                return new GreetingObserver();
            }
        };
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
