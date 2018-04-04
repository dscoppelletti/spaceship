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
 * Change password view-model.
 *
 * @since 1.0.0
 */
public final class ChangePasswordViewModel extends PasswordViewModel {

    /**
     * {@code Parcelable} support.
     */
    public static final Creator<ChangePasswordViewModel> CREATOR =
            new Creator<ChangePasswordViewModel>() {

                @Override
                public ChangePasswordViewModel createFromParcel(Parcel in) {
                    ChangePasswordViewModel obj;

                    obj = new ChangePasswordViewModel();
                    obj.readFromParcel(in);
                    return obj;
                }

                @Override
                public ChangePasswordViewModel[] newArray(int size) {
                    return new ChangePasswordViewModel[size];
                }
            };

    private final EditTextValidator myPwdOldValidator;
    private String myPwdOld;
    private int myPwdOldErr;

    /**
     * Sole constructor.
     */
    public ChangePasswordViewModel() {
        super();

        myPwdOldValidator = () -> {
            if (TextUtils.isEmpty(myPwdOld)) {
                setPasswordOldError(R.string.it_scoppelletti_err_passwordRequired);
                return false;
            }

            setPasswordOldError(0);
            return true;
        };
    }

    /**
     * Gets the old password.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public String getPasswordOld() {
        return myPwdOld;
    }

    /**
     * Sets the old password.
     *
     * @param value A value. May be {@code null}.
     */
    public void setPasswordOld(@Nullable String value) {
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
        myPwdOld = in.readString();
        myPwdOldErr = in.readInt();
        super.readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myPwdOld);
        dest.writeInt(myPwdOldErr);
        super.writeToParcel(dest, flags);
    }
}
