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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;

/**
 * Notifies that the login has been succeeded.
 *
 * @since 1.0.0
 */
public final class LoginEvent {
    private final CognitoUser myUser;

    /**
     * Constructor.
     */
    public LoginEvent(@NonNull CognitoUser user) {
        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }

        myUser = user;
    }

    /**
     * Gets the user.
     *
     * @return The object.
     */
    @NonNull
    public CognitoUser getUser() {
        return myUser;
    }
}
