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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.security.SecureString;

/**
 * Changes the password of a user.
 */
@Slf4j
final class ChangePasswordObservable implements
        ObservableOnSubscribe<Object>, GenericHandler {
    private final CognitoUser myUser;
    private final SecureString myPwdOld;
    private final SecureString myPwdNew;
    private ObservableEmitter<Object> myEmitter;

    /**
     * Constructor.
     *
     * @param user        The user
     * @param passwordOld The old password.
     * @param passwordNew The new password.
     */
    ChangePasswordObservable(@NonNull CognitoUser user,
            @NonNull SecureString passwordOld,
            @NonNull SecureString passwordNew) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }
        if (TextUtils.isEmpty(passwordOld)) {
            throw new NullPointerException("Argument passwordOld is null.");
        }
        if (TextUtils.isEmpty(passwordNew)) {
            throw new NullPointerException("Argument passwordNew is null.");
        }

        myUser = user;
        myPwdOld = passwordOld;
        myPwdNew = passwordNew;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter)
            throws Exception {
        myEmitter = emitter;

        try {
            // Amazon Cognito uses immutable strings for passwords
            myUser.changePassword(myPwdOld.toString(), myPwdNew.toString(),
                    this);
        } finally {
            myPwdOld.clear();
            myPwdNew.clear();
        }
    }

    @Override
    public void onSuccess() {
        myLogger.debug("Change password of the user {} succeeded.",
                myUser.getUserId());
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format("Failed to change password of the user " +
                "%1$s.", myUser.getUserId()), ex);
        myEmitter.onError(ex);
    }
}

