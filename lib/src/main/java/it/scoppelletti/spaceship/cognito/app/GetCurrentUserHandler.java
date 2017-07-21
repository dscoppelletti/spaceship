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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import io.reactivex.Emitter;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.cognito.LoginEvent;

/**
 * Handles the retrieving of the current user.
 */
@Slf4j
final class GetCurrentUserHandler implements AuthenticationHandler {
    private final CognitoUser myUser;
    private final Emitter<LoginEvent> myEmitter;

    /**
     * Constructor.
     *
     * @param user    The user.
     * @param emitter The {@code Emitter} interface.
     */
    GetCurrentUserHandler(@NonNull CognitoUser user,
            Emitter<LoginEvent> emitter) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }
        if (emitter == null) {
            throw new NullPointerException("Argument emitter is null.");
        }

        myUser = user;
        myEmitter = emitter;
    }

    @Override
    public void onSuccess(CognitoUserSession userSession,
            CognitoDevice newDevice) {
        myLogger.debug("User {} is still logged.", myUser.getUserId());
        myEmitter.onNext(new LoginEvent(myUser));
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format("User %1$s is not logged anymore.",
                myUser.getUserId()), ex);
        myEmitter.onComplete();
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation flow,
            String userId) {
        myLogger.debug("User {} is not logged anymore.", myUser.getUserId());
        myEmitter.onComplete();
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation flow) {
        myLogger.error("MFA not supported.");
        myEmitter.onComplete();
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation flow) {
        myLogger.debug("Will not respond to the challange {}.",
                flow.getChallengeName());
        myEmitter.onComplete();
    }
}
