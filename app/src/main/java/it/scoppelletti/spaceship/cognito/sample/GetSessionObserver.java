package it.scoppelletti.spaceship.cognito.sample;

import android.support.annotation.NonNull;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleObserverFactory;

@Slf4j
final class GetSessionObserver extends
        DisposableSingleObserver<CognitoUserSession> {

    private GetSessionObserver() {
    }

    @NonNull
    static SingleObserverFactory<CognitoUserSession> newFactory() {
        return new SingleObserverFactory<CognitoUserSession>() {

            @NonNull
            @Override
            public DisposableSingleObserver<CognitoUserSession> create() {
                return new GetSessionObserver();
            }
        };
    }

    @Override
    public void onSuccess(@NonNull CognitoUserSession session) {
        myLogger.info("idToken={}", session.getIdToken().getJWTToken());
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error("Failed to get session.", ex);
        EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(R.string.cmd_getSession));
    }
}
