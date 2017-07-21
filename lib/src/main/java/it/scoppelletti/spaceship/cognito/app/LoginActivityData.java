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
import it.scoppelletti.spaceship.cognito.LoginEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

/**
 * Data retained across activities for the user authentication process.
 *
 * @since 1.0.0
 */
public final class LoginActivityData extends Fragment {
    private SingleCoordinator<Object> myLoginCoordinator;
    private SingleCoordinator<LoginEvent> myCurrentUserCoordinator;
    private SingleCoordinator<Object> myResetPwdCoordinator;

    /**
     * Sole constructor.
     */
    public LoginActivityData() {
        setRetainInstance(true);
    }

    /**
     * Gets the coordinator for user authentication process.
     *
     * @return The object.
     */
    @NonNull
    SingleCoordinator<Object> getLoginCoordinator() {
        if (myLoginCoordinator == null) {
            myLoginCoordinator = new SingleCoordinator<>();
        }

        return myLoginCoordinator;
    }

    /**
     * Gets the coordinator for retrieving the current user.
     *
     * @return The object.
     */
    @NonNull
    SingleCoordinator<LoginEvent> getCurrentUserCoordinator() {
        if (myCurrentUserCoordinator == null) {
            myCurrentUserCoordinator = new SingleCoordinator<>();
        }

        return myCurrentUserCoordinator;
    }

    /**
     * Gets the coordinator for reset password process.
     *
     * @return The object.
     */
    @NonNull
    SingleCoordinator<Object> getResetPasswordCoordinator() {
        if (myResetPwdCoordinator == null) {
            myResetPwdCoordinator = new SingleCoordinator<>();
        }

        return myResetPwdCoordinator;
    }

    @Override
    public void onDestroy() {
        if (myLoginCoordinator != null) {
            myLoginCoordinator.onDestroy();
            myLoginCoordinator = null;
        }
        if (myCurrentUserCoordinator != null) {
            myCurrentUserCoordinator.onDestroy();
            myCurrentUserCoordinator = null;
        }
        if (myResetPwdCoordinator != null) {
            myResetPwdCoordinator.onDestroy();
            myResetPwdCoordinator = null;
        }

        super.onDestroy();
    }
}
