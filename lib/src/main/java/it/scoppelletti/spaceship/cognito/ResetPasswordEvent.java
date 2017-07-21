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

package it.scoppelletti.spaceship.cognito;

import android.support.annotation.NonNull;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;

/**
 * Notifies that user should be prompted for the validation code and a new
 * password.
 *
 * @since 1.0.0
 */
public final class ResetPasswordEvent {
    private final ForgotPasswordContinuation myFlow;

    /**
     * Constructor.
     *
     * @param flow State of the flow.
     */
    public ResetPasswordEvent(@NonNull ForgotPasswordContinuation flow) {
        if (flow == null) {
            throw new NullPointerException("Argument flow is null.");
        }

        myFlow = flow;
    }

    /**
     * Gets the state of the flow.
     *
     * @return The object.
     */
    @NonNull
    public ForgotPasswordContinuation getFlow() {
        return myFlow;
    }
}
