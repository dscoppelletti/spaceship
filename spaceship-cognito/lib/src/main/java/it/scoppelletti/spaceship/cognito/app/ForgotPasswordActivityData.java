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
import android.support.v4.app.Fragment;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

/**
 * Data retained across activities for the resetting password process.
 *
 * @see   it.scoppelletti.spaceship.cognito.app.ForgotPasswordActivity
 * @since 1.0.0
 */
public final class ForgotPasswordActivityData extends Fragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = CognitoAdapter.TAG_FORGOTPASSWORDDATA;

    private CompletableCoordinator myPwdCoordinator;
    private SingleCoordinator<ForgotPasswordContinuation>
            myVerificationCodeCoordinator;

    /**
     * Sole constructor.
     */
    public ForgotPasswordActivityData() {
        setRetainInstance(true);
    }

    /**
     * Gets the coordinator for resetting password.
     *
     * @return The object.
     */
    @NonNull
    CompletableCoordinator getPasswordCoordinator() {
        if (myPwdCoordinator == null) {
            myPwdCoordinator = new CompletableCoordinator();
        }

        return myPwdCoordinator;
    }

    /**
     * Gets the coordinator for sending a verification code.
     *
     * @return The object.
     */
    @NonNull
    SingleCoordinator<ForgotPasswordContinuation>
    getVerificationCodeCoordinator() {
        if (myVerificationCodeCoordinator == null) {
            myVerificationCodeCoordinator = new SingleCoordinator<>();
        }

        return myVerificationCodeCoordinator;
    }

    @Override
    public void onDestroy() {
        if (myPwdCoordinator != null) {
            myPwdCoordinator.onDestroy();
            myPwdCoordinator = null;
        }
        if (myVerificationCodeCoordinator != null) {
            myVerificationCodeCoordinator.onDestroy();
            myVerificationCodeCoordinator = null;
        }

        super.onDestroy();
    }
}
