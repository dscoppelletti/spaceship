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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.security.SecureString;

/**
 * Adapter for Amazon Cognito SDK.
 *
 * @since 1.0.0
 */
@Slf4j
public final class CognitoAdapter {

    /**
     * Property reporting the delivery medium through which the validation code
     * was sent.
     */
    public static final String PROP_DELIVERYMEDIUM =
            "it.scoppelletti.spaceship.cognito.1";

    /**
     * Property reporting the destination to which the validation code was sent.
     */
    public static final String PROP_DESTINATION =
            "it.scoppelletti.spaceship.cognito.2";

    /**
     * Property reporting the new password.
     */
    public static final String PROP_PASSWORDNEW =
            "it.scoppelletti.spaceship.cognito.3";

    /**
     * Property reporting the user attributes.
     */
    public static final String PROP_USERATTRIBUTES =
            "it.scoppelletti.spaceship.cognito.4";

    /**
     * Property reporting the validation code.
     */
    public static final String PROP_VERIFICATIONCODE =
            "it.scoppelletti.spaceship.cognito.5";

    /**
     * Tag of the {@code LoginActivityData} fragment.
     *
     * @see it.scoppelletti.spaceship.cognito.app.LoginActivityData
     */
    public static final String TAG_LOGINDATA =
            "it.scoppelletti.spaceship.cognito.1";

    private static CognitoAdapter myInstance;
    private final CognitoUserPool myUserPool;
    private CognitoUser myCurrentUser;

    /**
     * Constructor.
     *
     * @param builder The instance builder.
     */
    private CognitoAdapter(CognitoAdapter.Builder builder) {
        // Amazon Cognito uses immutable string for client secret
        myUserPool = new CognitoUserPool(builder.myCtx, builder.myPoolId,
                builder.myClientId, builder.myClientSecret.toString(),
                builder.myRegion);
    }

    /**
     * Gets the instance.
     *
     * @return The object.
     */
    @NonNull
    public static CognitoAdapter getInstance() {
        if (myInstance == null) {
            throw new NullPointerException("CognitoAdapter instance not set.");
        }

        return myInstance;
    }

    /**
     * Gets the {@code CognitoUserPool} object.
     *
     * @return The object.
     */
    @NonNull
    public CognitoUserPool getUserPool() {
        return myUserPool;
    }

    /**
     * Gets the current user.
     *
     * @return The object. May be {@code null}.
     */
    @Nullable
    public CognitoUser getCurrentUser() {
        return myCurrentUser;
    }

    /**
     * Sets the current user.
     *
     * @param event The event.
     */
    public void setCurrentUser(@NonNull LoginEvent event) {
        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        myCurrentUser = event.getUser();
    }

    /**
     * Resets the current user.
     */
    public void resetCurrentUser() {
        myCurrentUser = null;
    }

    /**
     * Logout the current user.
     */
    public void logout() {
        if (myCurrentUser == null)  {
            myLogger.warn("No user currently logged.");
            return;
        }

        myCurrentUser.signOut();
        myCurrentUser = null;
    }

    /**
     * Builds a {@code CognitoAdapter} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final Context myCtx;
        private String myPoolId;
        private String myClientId;
        private SecureString myClientSecret;
        private Regions myRegion;

        /**
         * Constructor.
         *
         * @param ctx Context.
         */
        public Builder(@NonNull Context ctx) {
            if (ctx == null) {
                throw new NullPointerException("Argument ctx is null.");
            }

            myCtx = ctx.getApplicationContext();
        }

        /**
         * Sets the pool-id of the user-pool.
         *
         * @param  value The value.
         * @return       This object.
         */
        @NonNull
        public CognitoAdapter.Builder poolId(@NonNull String value) {
            if (TextUtils.isEmpty(value)) {
                throw new NullPointerException("Argument value is null.");
            }

            myPoolId = value;
            return this;
        }

        /**
         * Sets the client-id generated for this app and this user-pool.
         *
         * @param  value The value.
         * @return       This object.
         */
        @NonNull
        public CognitoAdapter.Builder clientId(@NonNull String value) {
            if (TextUtils.isEmpty(value)) {
                throw new NullPointerException("Argument value is null.");
            }

            myClientId = value;
            return this;
        }

        /**
         * Sets the client secret generated fot this app and this user-pool.
         *
         * @param  value The value.
         * @return       This object.
         */
        @NonNull
        public CognitoAdapter.Builder clientSecret(
                @NonNull SecureString value) {
            if (TextUtils.isEmpty(value)) {
                throw new NullPointerException("Argument value is null.");
            }

            myClientSecret = value;
            return this;
        }

        /**
         * Sets the region of this user-pool.
         *
         * @param  value The value.
         * @return       This object.
         */
        @NonNull
        public CognitoAdapter.Builder region(@NonNull Regions value) {
            if (value == null) {
                throw new NullPointerException("Argument value is null.");
            }

            myRegion = value;
            return this;
        }

        /**
         * Builds the singleton {@code CognitoAdapter} instance.
         *
         * @return The new object.
         */
        @NonNull
        public CognitoAdapter build() {
            if (myInstance != null) {
                throw new IllegalStateException(
                        "CognitoAdapter instance already set.");
            }

            if (myPoolId == null) {
                throw new NullPointerException("Property poolId is null.");
            }
            if (myClientId == null) {
                throw new NullPointerException("Property clientId is null.");
            }
            if (myClientSecret == null) {
                throw new NullPointerException(
                        "Property clientSecret is null.");
            }
            if (myRegion == null) {
                throw new NullPointerException("Property region is null.");
            }

            myInstance = new CognitoAdapter(this);
            myClientSecret.clear();
            return myInstance;
        }
    }
}

