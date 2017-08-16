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
import io.reactivex.observers.DisposableCompletableObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.rx.CompletableObserverFactory;
import it.scoppelletti.spaceship.rx.CompleteEvent;

/**
 * Observer for logout process.
 */
@Slf4j
final class LogoutObserver extends DisposableCompletableObserver {

    /**
     * Sole constructor.
     */
    private LogoutObserver() {
    }

    /**
     * Creates a new factory object for creating instances of the
     * {@code GetCurrentUserObserver} class.
     *
     * @return The new object.
     */
    @NonNull
    static CompletableObserverFactory newFactory() {
        return new CompletableObserverFactory() {

            @NonNull
            @Override
            public DisposableCompletableObserver create() {
                return new LogoutObserver();
            }
        };
    }

    @Override
    public void onComplete() {
        myLogger.debug("Logout succeeded.");
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error("Failed to logout.", ex);
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }
}
