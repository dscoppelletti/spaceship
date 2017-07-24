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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

/**
 * Data retained across activities for the user authentication process.
 *
 * @see   it.scoppelletti.spaceship.cognito.app.LoginActivityBase
 * @since 1.0.0
 */
public final class LoginActivityData extends Fragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = CognitoAdapter.TAG_LOGINDATA;

    private CognitoUser myPendingUser;
    private NewPasswordEvent myNewPwdEvent;
    private SingleCoordinator<Object> myLoginCoordinator;
    private SingleCoordinator<CognitoUser> myCurrentUserCoordinator;
    private SingleCoordinator<GetUserDetailsEvent> myUserDetailsCoordinator;

    /**
     * Sole constructor.
     */
    public LoginActivityData() {
        setRetainInstance(true);
    }

    /**
     * Gets the user in process.
     *
     * @return The object. May be {@code null}.
     */
    @Nullable
    CognitoUser getPendingUser() {
        return myPendingUser;
    }

    /**
     * Sets the user in process.
     *
     * @param obj The object. May be {@code null}.
     */
    void setPendingUser(@Nullable CognitoUser obj) {
        myPendingUser = obj;
    }

    /**
     * Gets the pending request for a new password.
     *
     * @return The object. May be {@code null}.
     */
    @Nullable
    NewPasswordEvent getNewPasswordEvent() {
        return myNewPwdEvent;
    }

    /**
     * Sets the pending request for a new password.
     *
     * @param obj The object. May be {@code null}.
     */
    void setNewPasswordEvent(@Nullable NewPasswordEvent obj) {
        myNewPwdEvent = obj;
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
    SingleCoordinator<CognitoUser> getCurrentUserCoordinator() {
        if (myCurrentUserCoordinator == null) {
            myCurrentUserCoordinator = new SingleCoordinator<>();
        }

        return myCurrentUserCoordinator;
    }

    /**
     * Gets the coordinator for retrieving the detail of the current user.
     *
     * @return The object.
     */
    @NonNull
    SingleCoordinator<GetUserDetailsEvent> getUserDetailsCoordinator() {
        if (myUserDetailsCoordinator == null) {
            myUserDetailsCoordinator = new SingleCoordinator<>();
        }

        return myUserDetailsCoordinator;
    }

    @Override
    public void onDestroy() {
        myPendingUser = null;
        myNewPwdEvent = null;

        if (myLoginCoordinator != null) {
            myLoginCoordinator.onDestroy();
            myLoginCoordinator = null;
        }
        if (myCurrentUserCoordinator != null) {
            myCurrentUserCoordinator.onDestroy();
            myCurrentUserCoordinator = null;
        }
        if (myUserDetailsCoordinator != null) {
            myUserDetailsCoordinator.onDestroy();
            myUserDetailsCoordinator = null;
        }

        super.onDestroy();
    }
}
