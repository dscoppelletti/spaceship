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

/**
 * User attribute.
 *
 * @since 1.0.0
 */
public final class UserAttribute {

    /**
     * Attribute reporting the address.
     */
    public static final String ATTR_ADDRESS = "address";

    /**
     * Attribute reporting the birthdate.
     */
    public static final String ATTR_BIRTHDATE = "birthdate";

    /**
     * Attribute reporting the email.
     */
    public static final String ATTR_EMAIL = "email";

    /**
     * Attribute reporting whether the email has been verified.
     */
    public static final String ATTR_EMAIL_VERIFIED = "email_verified";

    /**
     * Attribute reporting the gender.
     */
    public static final String ATTR_GENDER = "gender";

    /**
     * Attribute reporting the locale.
     */
    public static final String ATTR_LOCALE = "locale";

    /**
     * Attribute reporting the middle name.
     */
    public static final String ATTR_MIDDLENAME = "middle_name";

    /**
     * Attribute reporting the name.
     */
    public static final String ATTR_NAME = "given_name";

    /**
     * Attribute reporting the nickname.
     */
    public static final String ATTR_NICKNAME = "nickname";

    /**
     * Attribute reporting the phone number.
     */
    public static final String ATTR_PHONENUMBER = "phone_number";

    /**
     * Attribute reporting whether the phone number has been verified.
     */
    public static final String ATTR_PHONENUMBER_VERIFIED =
            "phone_number_verified";

    /**
     * Attribute reporting the picture.
     */
    public static final String ATTR_PICTURE = "picture";

    /**
     * Attribute reporting the preferred user code.
     */
    public static final String ATTR_PREFEREEDUSERCODE = "preferred_username";

    /**
     * Attribute reporting the profile.
     */
    public static final String ATTR_PROFILE = "profile";

    /**
     * Attribute reporting the surname.
     */
    public static final String ATTR_SURNAME = "family_name";

    /**
     * Attribute reporting the time-zone.
     */
    public static final String ATTR_TIMEZONE = "zoneinfo";

    /**
     * Attribute reporting the time when the user data has been updated.
     */
    public static final String ATTR_UPDATEDAT = "updated_at";

    /**
     * Attribute reporting the user code.
     */
    public static final String ATTR_USERCODE = "name";

    /**
     * Attribute reporting the website.
     */
    public static final String ATTR_WEBSITE = "website";

    /**
     * Private constructor for static class.
     */
    private UserAttribute() {
    }
}
