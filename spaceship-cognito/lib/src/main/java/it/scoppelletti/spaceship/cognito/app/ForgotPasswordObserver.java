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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import io.reactivex.observers.DisposableSingleObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Observer for send verification code process.
 */
@Slf4j
final class ForgotPasswordObserver extends
        DisposableSingleObserver<ForgotPasswordContinuation> {

    /**
     * Sole constructor.
     */
    ForgotPasswordObserver() {
    }

    @Override
    public void onSuccess(@NonNull ForgotPasswordContinuation event) {
        myLogger.debug("Send verification code for resetting password " +
                "succeeded.");
        EventBus.getDefault().post(new SnackbarEvent(
                R.string.it_scoppelletti_cognito_msg_verificationCode,
                Snackbar.LENGTH_SHORT)
                .messageArguments(event.getParameters().getDeliveryMedium(),
                        event.getParameters().getDestination()));
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error("Failed to send verification code for resetting " +
                        "password.", ex);
        EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(R.string.it_scoppelletti_cmd_forgotPassword));
    }
}
