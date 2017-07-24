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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import it.scoppelletti.spaceship.cognito.CognitoHandler;

/**
 * Prompts the user for a new password.
 *
 * @since 1.0.0
 */
public final class NewPasswordEvent {
    private final NewPasswordContinuation myFlow;
    private final CognitoHandler<Object> myHandler;

    /**
     * Constructor.
     *
     * @param flow    State of the flow.
     * @param handler The {@code CognitoHandler} interface.
     */
    NewPasswordEvent(@NonNull NewPasswordContinuation flow,
            @NonNull CognitoHandler<Object> handler) {
        if (flow == null) {
            throw new NullPointerException("Argument flow is null.");
        }
        if (handler == null) {
            throw new NullPointerException("Argument handler is null.");
        }

        myFlow = flow;
        myHandler = handler;
    }

    /**
     * Gets the state of the flow.
     *
     * @return The object.
     */
    @NonNull
    NewPasswordContinuation getFlow() {
        return myFlow;
    }

    /**
     * Gets the {@code CognitoHandler} interface.
     *
     * @return The object.
     */
    @NonNull
    CognitoHandler<Object> getHandler() {
        return myHandler;
    }
}
