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
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoServiceConstants;
import io.reactivex.Emitter;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.cognito.LoginEvent;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.security.SecureString;

/**
 * Handles the user authentication process.
 */
@Slf4j
final class LoginHandler implements AuthenticationHandler {
    private final CognitoUser myUser;
    private final SecureString myPwd;

    /**
     * Constructor.
     *
     * @param user The user.
     * @param pwd  The password.
     */
    LoginHandler(@NonNull CognitoUser user, @NonNull SecureString pwd) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }
        if (TextUtils.isEmpty(pwd)) {
            throw new NullPointerException("Argument pwd is null.");
        }

        myUser = user;
        myPwd = pwd;
    }

    @Override
    public void onSuccess(CognitoUserSession userSession,
            CognitoDevice newDevice) {
        Emitter<Object> emitter;

        myLogger.debug("Login succeeded.");
        myPwd.clear();
        emitter = ThreadLocalEmitter.getInstance().get();
        emitter.onNext(new LoginEvent(myUser));
        emitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        Emitter<Object> emitter;

        myLogger.error("Login failed.", ex);
        myPwd.clear();
        emitter = ThreadLocalEmitter.getInstance().get();
        emitter.onError(ex);
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation flow,
            String userId) {
        AuthenticationDetails details;

        // Amazon Cognito uses immutable strings for passwords
        details = new AuthenticationDetails(userId, myPwd.toString(), null);
        myPwd.clear();
        flow.setAuthenticationDetails(details);
        flow.continueTask();
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation flow) {
        Throwable ex;
        Emitter<Object> emitter;

        myLogger.error("MFA not supported.");
        ex = new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_mfaNotSupported)
                .title(R.string.it_scoppelletti_cmd_login).build();
        emitter = ThreadLocalEmitter.getInstance().get();
        myPwd.clear();
        emitter.onError(ex);
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation flow) {
        Throwable ex;
        Emitter<Object> emitter;

        emitter = ThreadLocalEmitter.getInstance().get();
        switch (flow.getChallengeName()) {
        case CognitoServiceConstants.CHLG_TYPE_NEW_PASSWORD_REQUIRED:
            myLogger.debug("Requiring new password.");
            emitter.onNext(flow);
            emitter.onComplete();
            break;

        default:
            myLogger.error("Challenge {} not supported.",
                    flow.getChallengeName());
            myPwd.clear();
            ex = new ApplicationException.Builder(
                    R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                    .messageArguments(flow.getChallengeName())
                    .title(R.string.it_scoppelletti_cmd_login).build();
            emitter.onError(ex);
            break;
        }
    }
}
