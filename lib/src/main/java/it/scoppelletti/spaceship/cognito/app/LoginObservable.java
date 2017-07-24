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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoServiceConstants;
import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.CognitoHandler;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.security.SecureString;

/**
 * User authentication process.
 */
@Slf4j
final class LoginObservable implements ObservableOnSubscribe<Object>,
        CognitoHandler<Object>, AuthenticationHandler {
    private final CognitoUser myUser;
    private final SecureString myPwd;
    private Emitter<Object> myEmitter;

    /**
     * Constructor.
     *
     * @param userCode The user code.
     * @param pwd      The password.
     */
    LoginObservable(@NonNull String userCode, @NonNull SecureString pwd) {
        CognitoUserPool userPool;

        if (TextUtils.isEmpty(userCode)) {
            throw new NullPointerException("Argument userCode is null.");
        }
        if (TextUtils.isEmpty(pwd)) {
            throw new NullPointerException("Argument password is null.");
        }

        userPool = CognitoAdapter.getInstance().getUserPool();
        myUser = userPool.getUser(userCode);
        myPwd = pwd;
    }

    @Override
    public void setEmitter(@NonNull Emitter<Object> obj) {
        if (obj == null) {
            throw new NullPointerException("Argument obj is null.");
        }

        myEmitter = obj;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws
            Exception {
        myEmitter = emitter;
        myUser.getSession(this);
    }

    @Override
    public void onSuccess(CognitoUserSession userSession,
            CognitoDevice newDevice) {
        myLogger.debug("Login succeeded.");
        myPwd.clear();
        myEmitter.onNext(myUser);
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error("Login failed.", ex);
        myPwd.clear();
        myEmitter.onError(ex);
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

        myLogger.error("MFA not supported.");
        ex = new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_mfaNotSupported)
                .title(R.string.it_scoppelletti_cmd_login).build();
        myPwd.clear();
        myEmitter.onError(ex);
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation flow) {
        Throwable ex;

        switch (flow.getChallengeName()) {
        case CognitoServiceConstants.CHLG_TYPE_NEW_PASSWORD_REQUIRED:
            myLogger.debug("Requiring new password.");
            myEmitter.onNext(new NewPasswordEvent(
                    (NewPasswordContinuation) flow, this));
            myEmitter.onComplete();
            break;

        default:
            myLogger.error("Challenge {} not supported.",
                    flow.getChallengeName());
            myPwd.clear();
            ex = new ApplicationException.Builder(
                    R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                    .messageArguments(flow.getChallengeName())
                    .title(R.string.it_scoppelletti_cmd_login).build();
            myEmitter.onError(ex);
            break;
        }
    }
}
