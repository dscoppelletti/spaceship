/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.cognito.app;

import android.support.annotation.NonNull;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.LoginEvent;
import it.scoppelletti.spaceship.cognito.NewPasswordEvent;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.rx.SingleObserverFactory;

/**
 * Observer for user authentication process.
 */
@Slf4j
final class LoginObserver extends DisposableSingleObserver<Object> {

    /**
     * Sole constructor.
     */
    private LoginObserver() {
    }

    /**
     * Creates a new factory object for creating instances of the
     * {@code LoginObserver} class.
     *
     * @return The new object.
     */
    @NonNull
    static SingleObserverFactory<Object> newFactory() {
        return new SingleObserverFactory<Object>() {

            @NonNull
            @Override
            public DisposableSingleObserver<Object> create() {
                return new LoginObserver();
            }
        };
    }

    @Override
    public void onSuccess(@NonNull Object obj) {
        Throwable ex;
        CognitoAdapter adapter;

        adapter = CognitoAdapter.getInstance();
        if (obj instanceof LoginEvent) {
            myLogger.debug("Login succeeded.");
            adapter.setCurrentUser((LoginEvent) obj);
            EventBus.getDefault().post(obj);
        } else if (obj instanceof NewPasswordContinuation) {
            myLogger.debug("Requiring new password.");
            EventBus.getDefault().post(new NewPasswordEvent(
                    (NewPasswordContinuation) obj));
        } else {
            myLogger.error("Event {} not supported.", obj);
            ex = new ApplicationException.Builder(
                    R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                    .messageArguments((obj == null) ? null : obj.getClass())
                    .title(R.string.it_scoppelletti_cmd_login).build();
            EventBus.getDefault().post(new ExceptionEvent(ex));
        }
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error("Login failed.", ex);
        EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(R.string.it_scoppelletti_cmd_login));
    }
}
