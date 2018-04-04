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

package it.scoppelletti.spaceship.cognito;

import android.support.annotation.NonNull;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import it.scoppelletti.spaceship.ApplicationException;

/**
 * Gets a session.
 */
final class GetSessionHandler implements AuthenticationHandler {
    private CognitoUserSession mySession;

    /**
     * Sole constructor.
     */
    GetSessionHandler() {
    }

    /**
     * Gets the session.
     *
     * @return The object.
     */
    @NonNull
    CognitoUserSession getSession() {
        if (mySession == null) {
            throw new NullPointerException("No current session.");
        }

        return mySession;
    }

    @Override
    public void onSuccess(CognitoUserSession userSession,
            CognitoDevice newDevice) {
        mySession = userSession;
    }

    @Override
    public void onFailure(Exception ex) {
        throw new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_getSessionFailed)
                .title(R.string.it_scoppelletti_cmd_login).build();
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation flow,
            String userId) {
        throw new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                .messageArguments(flow.getClass().getName())
                .title(R.string.it_scoppelletti_cmd_login).build();
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
        throw new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_mfaNotSupported)
                .title(R.string.it_scoppelletti_cmd_login).build();
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation flow) {
        throw new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                .messageArguments(flow.getChallengeName())
                .title(R.string.it_scoppelletti_cmd_login).build();
    }
}
