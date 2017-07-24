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

package it.scoppelletti.spaceship.cognito.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;

/**
 * User.
 *
 * @since 1.0.0
 */
public final class SpaceshipUser {
    private final CognitoUser myUser;
    private final CognitoUserAttributes myAttrs;

    /**
     * Constructor.
     *
     * @param user    User object.
     * @param details User details. May be {@code null}.
     */
    public SpaceshipUser(@NonNull CognitoUser user,
            @Nullable CognitoUserDetails details) {
        if (user == null || TextUtils.isEmpty(user.getUserId())) {
            throw new NullPointerException("Argument user is null.");
        }

        myUser = user;
        myAttrs = (details == null) ? null : details.getAttributes();
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

    /**
     * Gets the user code.
     *
     * @return The value.
     */
    @NonNull
    public String getUserCode() {
        String value;

        value = getAttribute(UserAttribute.ATTR_USERCODE);
        if (!TextUtils.isEmpty(value)) {
            return value;
        }

        value = getAttribute(UserAttribute.ATTR_PREFEREEDUSERCODE);
        if (!TextUtils.isEmpty(value)) {
            return value;
        }

        value = getAttribute(UserAttribute.ATTR_NICKNAME);
        if (!TextUtils.isEmpty(value)) {
            return value;
        }

        return myUser.getUserId();
    }

    /**
     * Gets the full name.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getFullName() {
        String value;
        StringBuilder buf;

        buf = new StringBuilder();
        value =  getAttribute(UserAttribute.ATTR_NAME);
        if (!TextUtils.isEmpty(value)) {
            buf.append(value);
        }

        value = getAttribute(UserAttribute.ATTR_MIDDLENAME);
        if (!TextUtils.isEmpty(value)) {
            if (buf.length() > 0) {
                buf.append(' ');
            }

            buf.append(value);
        }

        value = getAttribute(UserAttribute.ATTR_SURNAME);
        if (!TextUtils.isEmpty(value)) {
            if (buf.length() > 0) {
                buf.append(' ');
            }

            buf.append(value);
        }

        if (buf.length() == 0) {
            return null;
        }

        return buf.toString();
    }

    /**
     * Gets the email.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getEmail() {
        return getAttribute(UserAttribute.ATTR_EMAIL);
    }

    /**
     * Indicates whether the email is verified or not.
     *
     * @return Returns {@code true} if the email is verified, {@code false}
     *         otherwise.
     */
    public boolean isEmailVerified() {
        return Boolean.parseBoolean(getAttribute(
                UserAttribute.ATTR_EMAIL_VERIFIED));
    }

    /**
     * Sets whether the email is verified or not.
     *
     * @param value Whether the email is verified.
     */
    public void setEmailVerified(boolean value) {
        setAttribute(UserAttribute.ATTR_EMAIL_VERIFIED,
                Boolean.toString(value));
    }

    /**
     * Gets the phone number.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getPhoneNumber() {
        return getAttribute(UserAttribute.ATTR_PHONENUMBER);
    }

    /**
     * Indicates whether the phone number is verified or not.
     *
     * @return Returns {@code true} if the phone number is verified,
     *         {@code false} otherwise.
     */
    public boolean isPhoneNumberVerified() {
        return Boolean.parseBoolean(getAttribute(
                UserAttribute.ATTR_PHONENUMBER_VERIFIED));
    }

    /**
     * Sets whether the phone number is verified or not.
     *
     * @param value Whether the phone number is verified.
     */
    public void setPhoneNumberVerified(boolean value) {
        setAttribute(UserAttribute.ATTR_PHONENUMBER_VERIFIED,
                Boolean.toString(value));
    }

    /**
     * Gets an attrinute.
     *
     * @param  key Key.
     * @return     Value. May be {@code null}.
     */
    private String getAttribute(String key) {
        if (myAttrs == null) {
            return null;
        }

        return myAttrs.getAttributes().get(key);
    }

    /**
     * Sets an attribute.
     *
     * @param key   Key.
     * @param value Value.
     */
    private void setAttribute(String key, String value) {
        if (myAttrs == null) {
            throw new NullPointerException("User attributes not accessible.");
        }

        myAttrs.addAttribute(key, value);
    }
}
