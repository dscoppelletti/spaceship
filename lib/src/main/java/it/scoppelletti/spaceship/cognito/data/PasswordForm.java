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
 * Password form.
 *
 * @since 1.0.0
 */
public abstract class PasswordForm extends BaseObservable implements
        Parcelable {
    private final EditTextValidator myPwdNewValidator;
    private final EditTextValidator myPwdConfirmValidator;
    private String myPwdNew;
    private int myPwdNewErr;
    private String myPwdConfirm;
    private int myPwdConfirmErr;

    /**
     * Sole constructor.
     */
    protected PasswordForm() {
        myPwdNewValidator = new EditTextValidator() {

            @Override
            public boolean validate() {
                if (TextUtils.isEmpty(myPwdNew)) {
                    setPasswordNewError(R.string.it_scoppelletti_err_passwordRequired);
                    return false;
                }

                setPasswordNewError(0);
                return true;
            }
        };

        myPwdConfirmValidator = new EditTextValidator() {

            @Override
            public boolean validate() {
                if (TextUtils.isEmpty(myPwdConfirm)) {
                    setPasswordConfirmError(R.string.it_scoppelletti_err_passwordRequired);
                    return false;
                }

                setPasswordConfirmError(0);
                return true;
            }
        };
    }

    /**
     * Gets the new password.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public String getPasswordNew() {
        return myPwdNew;
    }

    /**
     * Sets the new password.
     *
     * @param value A value. May be {@code null}.
     */
    public void setPasswordNew(@Nullable String value) {
        if (!TextUtils.equals(value, myPwdNew)) {
            notifyPropertyChanged(BR.passwordNew);
            myPwdNew = value;
        }

        myPwdNewValidator.validate();
    }

    /**
     * Gets the error message about the new password.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public final int getPasswordNewError() {
        return myPwdNewErr;
    }

    /**
     * Sets the error message about the new password.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setPasswordNewError(int value) {
        if (value != myPwdNewErr) {
            myPwdNewErr = value;
            notifyPropertyChanged(BR.passwordNewError);
        }
    }

    /**
     * Gets the new password validator.
     *
     * @return The object.
     */
    @NonNull
    @Bindable
    public EditTextValidator getPasswordNewValidator() {
        return myPwdNewValidator;
    }

    /**
     * Gets the password confirm.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public String getPasswordConfirm() {
        return myPwdConfirm;
    }

    /**
     * Sets the password confirm.
     *
     * @param value A value. May be {@code null}.
     */
    public void setPasswordConfirm(@Nullable String value) {
        if (!TextUtils.equals(value, myPwdConfirm)) {
            notifyPropertyChanged(BR.passwordConfirm);
            myPwdConfirm = value;
        }

        myPwdConfirmValidator.validate();
    }

    /**
     * Gets the error message about the password confirm.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public int getPasswordConfirmError() {
        return myPwdConfirmErr;
    }

    /**
     * Sets the error message about the password confirm.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setPasswordConfirmError(int value) {
        if (value != myPwdConfirmErr) {
            myPwdConfirmErr = value;
            notifyPropertyChanged(BR.passwordConfirmError);
        }
    }

    /**
     * Gets the password confirm validator.
     *
     * @return The object.
     */
    @NonNull
    @Bindable
    public EditTextValidator getPasswordConfirmValidator() {
        return myPwdConfirmValidator;
    }

    /**
     * Validates this form.
     *
     * @return Returns {@code true} if this form is valid, {@code false}
     *         otherwise.
     */
    public boolean validate() {
        boolean valid = true;

        if (!myPwdNewValidator.validate()) {
            valid = false;
        }
        if (!myPwdConfirmValidator.validate()) {
            valid = false;
        }
        if (!valid) {
            return false;
        }

        if (!TextUtils.equals(myPwdNew, myPwdConfirm)) {
            setPasswordNewError(R.string.it_scoppelletti_err_passwordNotMatch);
            setPasswordConfirmError(
                    R.string.it_scoppelletti_err_passwordNotMatch);
            valid = false;
        }

        return valid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Load this object.
     *
     * @param in The input stream.
     */
    protected void readFromParcel(Parcel in) {
        myPwdNew = in.readString();
        myPwdNewErr = in.readInt();
        myPwdConfirm = in.readString();
        myPwdConfirmErr = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myPwdNew);
        dest.writeInt(myPwdNewErr);
        dest.writeString(myPwdConfirm);
        dest.writeInt(myPwdConfirmErr);
    }
}
