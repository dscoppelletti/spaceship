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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

/**
 * Data retained across activities for the verify attribute process.
 *
 * @see   it.scoppelletti.spaceship.cognito.app.VerifyAttributeActivity
 * @since 1.0.0
 */
public final class VerifyAttributeActivityData extends Fragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = CognitoAdapter.TAG_VERIFYATTRIBUTEDATA;

    private CompletableCoordinator myVerifyCoordinator;
    private SingleCoordinator<CognitoUserCodeDeliveryDetails>
            myVerificationCodeCoordinator;

    /**
     * Sole constructor.
     */
    public VerifyAttributeActivityData() {
        setRetainInstance(true);
    }

    /**
     * Gets the coordinator for verifying an attribute.
     *
     * @return The object.
     */
    @NonNull
    CompletableCoordinator getVerifyCoordinator() {
        if (myVerifyCoordinator == null) {
            myVerifyCoordinator = new CompletableCoordinator();
        }

        return myVerifyCoordinator;
    }

    /**
     * Gets the coordinator for sending a verification code.
     *
     * @return The object.
     */
    @NonNull
    SingleCoordinator<CognitoUserCodeDeliveryDetails>
    getVerificationCodeCoordinator() {
        if (myVerificationCodeCoordinator == null) {
            myVerificationCodeCoordinator = new SingleCoordinator<>();
        }

        return myVerificationCodeCoordinator;
    }

    @Override
    public void onDestroy() {
        if (myVerifyCoordinator != null) {
            myVerifyCoordinator.onDestroy();
            myVerifyCoordinator = null;
        }
        if (myVerificationCodeCoordinator != null) {
            myVerificationCodeCoordinator.onDestroy();
            myVerificationCodeCoordinator = null;
        }

        super.onDestroy();
    }
}
