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

import java.util.NoSuchElementException;
import android.support.annotation.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.LoginEvent;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleObserverFactory;

/**
 * Observer for retrieving of the current user.
 */
@Slf4j
final class GetCurrentUserObserver extends
        DisposableSingleObserver<LoginEvent> {

    /**
     * Sole constructor.
     */
    private GetCurrentUserObserver() {
    }

    /**
     * Creates a new factory object for creating instances of the
     * {@code GetCurrentUserObserver} class.
     *
     * @return The new object.
     */
    @NonNull
    static SingleObserverFactory<LoginEvent> newFactory() {
        return new SingleObserverFactory<LoginEvent>() {

            @NonNull
            @Override
            public DisposableSingleObserver<LoginEvent> create() {
                return new GetCurrentUserObserver();
            }
        };
    }

    @Override
    public void onSuccess(@NonNull LoginEvent event) {
        myLogger.debug("User {} is still logged.", event.getUser().getUserId());
        CognitoAdapter.getInstance().setCurrentUser(event);
        EventBus.getDefault().post(event);
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        if (ex instanceof NoSuchElementException) {
            myLogger.debug("No user currently logged.");
        } else {
            myLogger.error("Failed to get the current user.", ex);
        }

        CognitoAdapter.getInstance().resetCurrentUser();
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }
}
