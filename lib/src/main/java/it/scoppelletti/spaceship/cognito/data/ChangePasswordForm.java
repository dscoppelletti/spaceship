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

import android.databinding.Bindable;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import it.scoppelletti.spaceship.cognito.BR;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.widget.EditTextValidator;

/**
 * Change password form.
 *
 * @since 1.0.0
 */
public final class ChangePasswordForm extends PasswordForm {

    /**
     * {@code Parcelable} support.
     */
    public static final Creator<ChangePasswordForm> CREATOR =
            new Creator<ChangePasswordForm>() {

                @Override
                public ChangePasswordForm createFromParcel(Parcel in) {
                    ChangePasswordForm obj;

                    obj = new ChangePasswordForm();
                    obj.readFromParcel(in);
                    return obj;
                }

                @Override
                public ChangePasswordForm[] newArray(int size) {
                    return new ChangePasswordForm[size];
                }
            };

    private final EditTextValidator myPwdOldValidator;
    private CharSequence myPwdOld;
    private int myPwdOldErr;

    /**
     * Sole constructor.
     */
    public ChangePasswordForm() {
        super();

        myPwdOldValidator = new EditTextValidator() {

            @Override
            public boolean validate() {
                if (TextUtils.isEmpty(myPwdOld)) {
                    setPasswordOldError(R.string.it_scoppelletti_err_passwordRequired);
                    return false;
                }

                setPasswordOldError(0);
                return true;
            }
        };
    }

    /**
     * Gets the old password.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public CharSequence getPasswordOld() {
        return myPwdOld;
    }

    /**
     * Sets the old password.
     *
     * @param value A value. May be {@code null}.
     */
    public void setPasswordOld(@Nullable CharSequence value) {
        if (!TextUtils.equals(value, myPwdOld)) {
            notifyPropertyChanged(BR.passwordOld);
            myPwdOld = value;
        }

        myPwdOldValidator.validate();
    }

    /**
     * Gets the error message about the old password.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public final int getPasswordOldError() {
        return myPwdOldErr;
    }

    /**
     * Sets the error message about the old password.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setPasswordOldError(int value) {
        if (value != myPwdOldErr) {
            myPwdOldErr = value;
            notifyPropertyChanged(BR.passwordOldError);
        }
    }

    /**
     * Gets the old password validator.
     *
     * @return The object.
     */
    @NonNull
    @Bindable
    public EditTextValidator getPasswordOldValidator() {
        return myPwdOldValidator;
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        if (!myPwdOldValidator.validate()) {
            valid = false;
        }
        if (!super.validate()) {
            valid = false;
        }

        return valid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        myPwdOld = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        myPwdOldErr = in.readInt();
        super.readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(myPwdOld, dest, flags);
        dest.writeInt(myPwdOldErr);
        super.writeToParcel(dest, flags);
    }
}
