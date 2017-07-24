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
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.rx.SingleObserverFactory;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Observer for send verification code process.
 */
@Slf4j
final class VerificationCodeObserver extends
        DisposableSingleObserver<CognitoUserCodeDeliveryDetails> {
    private final String myAttr;

    /**
     * Constructor.
     *
     * @param attr Attribute to verify.
     */
    private VerificationCodeObserver(String attr) {
        myAttr = attr;
    }

    /**
     * Creates a new factory object for creating instances of the
     * {@code VerificationCodeObserver} class.
     *
     * @param  attr Attribute to verify.
     * @return      The new object.
     */
    @NonNull
    static SingleObserverFactory<CognitoUserCodeDeliveryDetails> newFactory(
            @NonNull final String attr) {
        if (TextUtils.isEmpty(attr)) {
            throw new NullPointerException("Argument attr is null.");
        }

        return new SingleObserverFactory<CognitoUserCodeDeliveryDetails>() {

            @NonNull
            @Override
            public DisposableSingleObserver<CognitoUserCodeDeliveryDetails>
            create() {
                return new VerificationCodeObserver(attr);
            }
        };
    }

    @Override
    public void onSuccess(@NonNull CognitoUserCodeDeliveryDetails
            deliveryInfo) {
        myLogger.debug("Send verification code for attribute {} succeeded.",
                myAttr);
        EventBus.getDefault().post(new SnackbarEvent(
                R.string.it_scoppelletti_cognito_msg_verificationCode,
                Snackbar.LENGTH_SHORT)
                .messageArguments(deliveryInfo.getDeliveryMedium(),
                        deliveryInfo.getDestination()));
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error(String.format("Failed to send verification code for " +
                        "attribute %1$s.", myAttr), ex);
        EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(VerifyAttributeActivity.getTitleId(myAttr)));
    }
}
