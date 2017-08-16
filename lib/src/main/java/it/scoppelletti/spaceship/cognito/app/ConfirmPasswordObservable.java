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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;

/**
 * Resets the password of a user.
 */
@Slf4j
final class ConfirmPasswordObservable implements
        ObservableOnSubscribe<Object>, ForgotPasswordHandler {
    private final String myUserCode;
    private final String myVerificationCode;
    private final String myPwd;
    private ObservableEmitter<Object> myEmitter;

    /**
     * Constructor.
     *
     * @param userCode         The user code.
     * @param password         The new password.
     * @param verificationCode The verification code.
     */
    ConfirmPasswordObservable(@NonNull String userCode,
            @NonNull String password, @NonNull String verificationCode) {
        if (TextUtils.isEmpty(userCode)) {
            throw new NullPointerException("Argument userCode is null.");
        }
        if (TextUtils.isEmpty(password)) {
            throw new NullPointerException("Argument password is null.");
        }
        if (TextUtils.isEmpty(verificationCode)) {
            throw new NullPointerException("Argument verificationCode is null.");
        }

        myUserCode = userCode;
        myPwd = password;
        myVerificationCode = verificationCode;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter)
            throws Exception {
        CognitoUser user;
        CognitoUserPool userPool;

        userPool = CognitoAdapter.getInstance().getUserPool();
        user = userPool.getUser(myUserCode);
        myEmitter = emitter;
        user.confirmPassword(myVerificationCode, myPwd, this);
    }

    @Override
    public void onSuccess() {
        myLogger.debug("Resetting password of the user {} succeeded.",
                myUserCode);
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format(
                "Failed to reset password of the user %1$s.", myUserCode), ex);
        myEmitter.onError(ex);
    }

    @Override
    public void getResetCode(ForgotPasswordContinuation flow) {
        RuntimeException ex;

        ex = new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                .messageArguments(flow.getClass().getName())
                .title(R.string.it_scoppelletti_cmd_forgotPassword).build();
        myEmitter.onError(ex);
    }
}

