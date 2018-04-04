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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;

/**
 * Loads the details of a user.
 */
@Slf4j
final class GetUserDetailsObservable implements
        ObservableOnSubscribe<GetUserDetailsEvent>, GetDetailsHandler {
    private final CognitoUser myUser;
    private Emitter<GetUserDetailsEvent> myEmitter;

    /**
     * Constructor.
     *
     * @param user The user.
     */
    GetUserDetailsObservable(@NonNull CognitoUser user) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }

        myUser = user;
    }

    @Override
    public void subscribe(
            @NonNull ObservableEmitter<GetUserDetailsEvent> emitter) throws
            Exception {
        myEmitter = emitter;
        myUser.getDetails(this);
    }

    @Override
    public void onSuccess(CognitoUserDetails userDetails) {
        myEmitter.onNext(new GetUserDetailsEvent(userDetails));
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format("Failed to load details of user %1$s.",
                myUser.getUserId()), ex);
        myEmitter.onNext(new GetUserDetailsEvent(null));
        myEmitter.onComplete();
    }
}
