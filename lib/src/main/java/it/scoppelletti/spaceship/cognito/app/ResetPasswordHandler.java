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

import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import io.reactivex.Emitter;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Handles the reset password process
 */
@Slf4j
final class ResetPasswordHandler implements ForgotPasswordHandler {
    private final String myUserCode;

    /**
     * Constructor.
     *
     * @param userCode The user code.
     */
    ResetPasswordHandler(String userCode) {
        if (TextUtils.isEmpty(userCode)) {
            throw new NullPointerException("Argument userCode is null.");
        }

        myUserCode = userCode;
    }

    @Override
    public void onSuccess() {
        Emitter<Object> emitter;

        myLogger.debug("Reset password succeeded.");
        emitter = ThreadLocalEmitter.getInstance().get();
        emitter.onNext(new SnackbarEvent(
                R.string.it_scoppelletti_cognito_msg_resetPassword,
                Snackbar.LENGTH_SHORT)
                .messageArguments(myUserCode));
        emitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        Emitter<Object> emitter;

        myLogger.error("Reset password failed.", ex);
        emitter = ThreadLocalEmitter.getInstance().get();
        emitter.onError(ex);
    }

    @Override
    public void getResetCode(ForgotPasswordContinuation flow) {
        Emitter<Object> emitter;

        myLogger.debug("Prompting the user.");
        emitter = ThreadLocalEmitter.getInstance().get();
        emitter.onNext(flow);
        emitter.onComplete();
    }
}
