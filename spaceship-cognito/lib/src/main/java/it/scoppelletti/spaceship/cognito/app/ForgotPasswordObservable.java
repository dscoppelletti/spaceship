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
 * Sends a verification code for resetting password.
 */
@Slf4j
final class ForgotPasswordObservable implements
        ObservableOnSubscribe<ForgotPasswordContinuation>,
        ForgotPasswordHandler {
    private final String myUserCode;
    private ObservableEmitter<ForgotPasswordContinuation> myEmitter;

    /**
     * Constructor.
     *
     * @param userCode The user code.
     */
    ForgotPasswordObservable(@NonNull String userCode) {
        if (TextUtils.isEmpty(userCode)) {
            throw new NullPointerException("Argument userCode is null.");
        }

        myUserCode = userCode;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<ForgotPasswordContinuation>
            emitter) throws Exception {
        CognitoUser user;
        CognitoUserPool userPool;

        userPool = CognitoAdapter.getInstance().getUserPool();
        user = userPool.getUser(myUserCode);
        myEmitter = emitter;
        user.forgotPassword(this);
    }

    @Override
    public void onSuccess() {
        RuntimeException ex;

        ex = new ApplicationException.Builder(
                R.string.it_scoppelletti_cognito_err_challengeNotSupported)
                .messageArguments("onSuccess")
                .title(R.string.it_scoppelletti_cmd_forgotPassword).build();
        myEmitter.onError(ex);
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format("Failed to send to %1$s the " +
                        "verification code for resetting password.",
                myUserCode), ex);
        myEmitter.onError(ex);
    }

    @Override
    public void getResetCode(ForgotPasswordContinuation flow) {
        myLogger.debug("Send to {} verification code for resetting password " +
                        "succeeded.",  myUserCode);
        myEmitter.onNext(flow);
        myEmitter.onComplete();
    }
}
