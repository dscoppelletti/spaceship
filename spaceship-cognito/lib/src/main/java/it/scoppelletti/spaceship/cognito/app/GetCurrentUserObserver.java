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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import io.reactivex.observers.DisposableMaybeObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.rx.CompleteEvent;

/**
 * Observer for retrieving of the current user.
 */
@Slf4j
final class GetCurrentUserObserver extends
        DisposableMaybeObserver<CognitoUser> {

    /**
     * Sole constructor.
     */
    GetCurrentUserObserver() {
    }

    @Override
    public void onSuccess(@NonNull CognitoUser user) {
        myLogger.debug("User {} is still logged.", user.getUserId());
        EventBus.getDefault().post(user);
    }

    @Override
    public void onComplete() {
        myLogger.debug("No user currently logged.");
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error("Failed to get the current user.", ex);
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }
}
