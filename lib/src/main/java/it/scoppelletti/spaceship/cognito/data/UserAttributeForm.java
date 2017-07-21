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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * User attribute form.
 *
 * @since 1.0.0
 */
public final class UserAttributeForm implements Parcelable {

    /**
     * The {@code Parcelable} support.
     */
    public static final Creator<UserAttributeForm> CREATOR =
            new Creator<UserAttributeForm>() {

        @Override
        public UserAttributeForm createFromParcel(Parcel in) {
            return new UserAttributeForm(in);
        }

        @Override
        public UserAttributeForm[] newArray(int size) {
            return new UserAttributeForm[size];
        }
    };

    private final String myKey;
    private final boolean myRequired;
    private String myCurrentValue;
    private String myEditingValue;
    private String myError;

    /**
     * Constructor.
     *
     * @param key      The key.
     * @param required Whether the attribute is required.
     */
    public UserAttributeForm(@NonNull  String key, boolean required) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("Argument key is null.");
        }

        myKey = key;
        myRequired = required;
    }

    /**
     * Costructor.
     *
     * @param in The input stream.
     */
    private UserAttributeForm(Parcel in) {
        myKey = in.readString();
        myRequired = (in.readInt() != 0);
        myCurrentValue = in.readString();
        myEditingValue = in.readString();
        myError = in.readString();
    }

    /**
     * Gets the key.
     *
     * @return The value.
     */
    @NonNull
    public String getKey() {
        return myKey;
    }

    /**
     * Indicates whether this attribute is required or optional
     *
     * @return Returns {@code true} if this attribute is required, {@code false}
     *         otherwise.
     */
    public boolean isRequired() {
        return myRequired;
    }

    /**
     * Gets the current value.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getCurrentValue() {
        return myCurrentValue;
    }

    /**
     * Sets the current value.
     *
     * @param value A value. May be {@code null}.
     */
    public void setCurrentValue(@Nullable String value) {
        myCurrentValue = value;
        myEditingValue = value;
    }

    /**
     * Gets the editing value.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getEditingValue() {
        return myEditingValue;
    }

    /**
     * Sets the editing value.
     *
     * @param value A value. May be {@code null}.
     */
    public void setEditingValue(@Nullable String value) {
        myEditingValue = value;
    }

    /**
     * Gets the error message.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getError() {
        return myError;
    }

    /**
     * Sets the error message.
     *
     * @param value A value. May be {@code null}.
     */
    public void setError(@Nullable String value) {
        myError = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myKey);
        dest.writeInt((myRequired) ? 1 : 0);
        dest.writeString(myCurrentValue);
        dest.writeString(myEditingValue);
        dest.writeString(myError);
    }
}
