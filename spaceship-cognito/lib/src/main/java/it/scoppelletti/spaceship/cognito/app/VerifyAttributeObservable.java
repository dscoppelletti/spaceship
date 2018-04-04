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

/**
 * Verify an attribute.
 */
@Slf4j
final class VerifyAttributeObservable implements
        ObservableOnSubscribe<Object>, GenericHandler {
    private final CognitoUser myUser;
    private final String myAttr;
    private final String myVerificationCode;
    private ObservableEmitter<Object> myEmitter;
    private boolean myFailed;

    /**
     * Constructor.
     *
     * @param user             The user.
     * @param attr             The attribute to verify.
     * @param verificationCode The verification code.
     */
    VerifyAttributeObservable(@NonNull CognitoUser user, @NonNull String attr,
            @NonNull String verificationCode) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }
        if (TextUtils.isEmpty(attr)) {
            throw new NullPointerException("Argument attr is null.");
        }
        if (TextUtils.isEmpty(verificationCode)) {
            throw new NullPointerException(
                    "Argument verificationCode is null.");
        }

        myUser = user;
        myAttr = attr;
        myVerificationCode = verificationCode;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws
            Exception {
        myEmitter = emitter;
        myFailed = false;
        myUser.verifyAttribute(myAttr, myVerificationCode, this);

        // - http://github.com/aws/aws-sdk-android/issues/266 - March 20, 2017
        // onSuccess callback is missing from verifyAttribute function
        if (!myFailed) {
            onSuccess();
        }
    }

    @Override
    public void onSuccess() {
        myLogger.debug("Verify attribute {} for user {} succeded.", myAttr,
                myUser.getUserId());
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myFailed = true;
        myLogger.error(String.format("Failed to verify attribute %2$s for " +
                        "user %1$s.", myUser.getUserId(), myAttr), ex);
        myEmitter.onError(ex);
    }
}
