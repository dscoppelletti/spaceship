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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends a verification code for verify an attribute.
 */
@Slf4j
final class VerificationCodeObservable implements
        ObservableOnSubscribe<CognitoUserCodeDeliveryDetails>,
        VerificationHandler {
    private final CognitoUser myUser;
    private final String myAttr;
    private ObservableEmitter<CognitoUserCodeDeliveryDetails> myEmitter;

    /**
     * Constructor.
     *
     * @param user The user.
     * @param attr The attribute to verify.
     */
    VerificationCodeObservable(@NonNull CognitoUser user,
            @NonNull String attr) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }
        if (TextUtils.isEmpty(attr)) {
            throw new NullPointerException("Argument attr is null.");
        }

        myUser = user;
        myAttr = attr;
    }

    @Override
    public void subscribe(
            @NonNull ObservableEmitter<CognitoUserCodeDeliveryDetails> emitter)
            throws Exception {
        myEmitter = emitter;
        myUser.getAttributeVerificationCode(myAttr, this);
    }

    @Override
    public void onSuccess(CognitoUserCodeDeliveryDetails deliveryInfo) {
        myLogger.debug(
                "Send to {} verification code for attribute {} succeeded.",
                myUser.getUserId(), myAttr);
        myEmitter.onNext(deliveryInfo);
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format("Failed to send to %1$s the " +
                        "verification code for attribute %2$s.",
                myUser.getUserId(), myAttr), ex);
        myEmitter.onError(ex);
    }
}
