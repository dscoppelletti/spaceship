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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import it.scoppelletti.spaceship.cognito.BR;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.widget.EditTextValidator;

/**
 * Login form.
 *
 * @since 1.0.0
 */
public final class LoginForm extends BaseObservable implements Parcelable {

    /**
     * The {@code Parcelable} support.
     */
    public static final Creator<LoginForm> CREATOR = new Creator<LoginForm>() {

        @Override
        public LoginForm createFromParcel(Parcel in) {
            return new LoginForm(in);
        }

        @Override
        public LoginForm[] newArray(int size) {
            return new LoginForm[size];
        }
    };

    private final EditTextValidator myUserCodeValidator;
    private final EditTextValidator myPwdValidator;
    private String myUserCode;
    private int myUserCodeErr;
    private CharSequence myPwd;
    private int myPwdErr;

    /**
     * Sole constructor.
     */
    public LoginForm() {
        myUserCodeValidator = new EditTextValidator() {

            @Override
            public boolean validate() {
                if (TextUtils.isEmpty(myUserCode)) {
                    setUserCodeError(R.string.it_scoppelletti_err_userCodeRequired);
                    return false;
                }

                setUserCodeError(0);
                return true;
            }
        };

        myPwdValidator = new EditTextValidator() {

            @Override
            public boolean validate() {
                if (TextUtils.isEmpty(myPwd)) {
                    setPasswordError(R.string.it_scoppelletti_err_passwordRequired);
                    return false;
                }

                setPasswordError(0);
                return true;
            }
        };
    }

    /**
     * Constructor.
     *
     * @param in The input stream.
     */
    private LoginForm(Parcel in) {
        this();

        myUserCode = in.readString();
        myUserCodeErr = in.readInt();
        myPwd = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        myPwdErr = in.readInt();
    }

    /**
     * Gets the user code.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public String getUserCode() {
        return myUserCode;
    }

    /**
     * Sets the user code.
     *
     * @param value A value. May be {@code null}.
     */
    public void setUserCode(@Nullable String value) {
        if (!TextUtils.equals(value, myUserCode)) {
            notifyPropertyChanged(BR.userCode);
            myUserCode = value;
        }

        myUserCodeValidator.validate();
    }

    /**
     * Gets the error message about the user code.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public int getUserCodeError() {
        return myUserCodeErr;
    }

    /**
     * Sets the error message about the user code.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setUserCodeError(int value) {
        if (value != myUserCodeErr) {
            myUserCodeErr = value;
            notifyPropertyChanged(BR.userCodeError);
        }
    }

    /**
     * Gets the user code validator.
     *
     * @return The object.
     */
    @NonNull
    @Bindable
    public EditTextValidator getUserCodeValidator() {
        return myUserCodeValidator;
    }

    /**
     * Gets the password.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public CharSequence getPassword() {
        return myPwd;
    }

    /**
     * Sets the password.
     *
     * @param value A value. May be {@code null}.
     */
    public void setPassword(@Nullable CharSequence value) {
        if (!TextUtils.equals(value, myPwd)) {
            notifyPropertyChanged(BR.password);
            myPwd = value;
        }

        myPwdValidator.validate();
    }

    /**
     * Gets the error message about the password.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public int getPasswordError() {
        return myPwdErr;
    }

    /**
     * Sets the error message about the password.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setPasswordError(int value) {
        if (value != myPwdErr) {
            myPwdErr = value;
            notifyPropertyChanged(BR.passwordError);
        }
    }

    /**
     * Gets the password validator.
     *
     * @return The object.
     */
    @NonNull
    @Bindable
    public EditTextValidator getPasswordValidator() {
        return myPwdValidator;
    }

    /**
     * Validates this form for login process.
     *
     * @return Returns {@code true} if this form is valid, {@code false}
     *         otherwise.
     */
    public boolean validateLogin() {
        boolean valid = true;

        if (!myUserCodeValidator.validate()) {
            valid = false;
        }
        if (!myPwdValidator.validate()) {
            valid = false;
        }

        return valid;
    }

    /**
     * Validates this form for forgot password process.
     *
     * @return Returns {@code true} if this form is valid, {@code false}
     *         otherwise.
     */
    public boolean validateForgotPassword() {
        setPasswordError(0);
        return myUserCodeValidator.validate();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myUserCode);
        dest.writeInt(myUserCodeErr);
        TextUtils.writeToParcel(myPwd, dest, 0);
        dest.writeInt(myPwdErr);
    }
}
