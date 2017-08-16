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
 * Forgot password form.
 *
 * @since 1.0.0
 */
public final class ForgotPasswordForm extends PasswordForm {

    /**
     * The {@code Parcelable} support.
     */
    public static final Creator<ForgotPasswordForm> CREATOR =
            new Creator<ForgotPasswordForm>() {

                @Override
                public ForgotPasswordForm createFromParcel(Parcel in) {
                    ForgotPasswordForm obj;

                    obj = new ForgotPasswordForm();
                    obj.readFromParcel(in);
                    return obj;
                }

                @Override
                public ForgotPasswordForm[] newArray(int size) {
                    return new ForgotPasswordForm[size];
                }
            };

    private final EditTextValidator myVerificationCodeValidator;
    private String myVerificationCode;
    private int myVerificationCodeErr;

    /**
     * Sole constructor.
     */
    public ForgotPasswordForm() {
        super();

        myVerificationCodeValidator = new EditTextValidator() {

            @Override
            public boolean validate() {
                if (TextUtils.isEmpty(myVerificationCode)) {
                    setVerificationCodeError(R.string.it_scoppelletti_cognito_err_verificationCodeRequired);
                    return false;
                }

                setVerificationCodeError(0);
                return true;
            }
        };
    }

    /**
     * Gets the verification code.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public String getVerificationCode() {
        return myVerificationCode;
    }

    /**
     * Sets the verification code.
     *
     * @param value A value. May be {@code null}.
     */
    public void setVerificationCode(@Nullable String value) {
        if (!TextUtils.equals(value, myVerificationCode)) {
            notifyPropertyChanged(BR.verificationCode);
            myVerificationCode = value;
        }

        myVerificationCodeValidator.validate();
    }

    /**
     * Gets the error message about the verification code.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public int getVerificationCodeError() {
        return myVerificationCodeErr;
    }

    /**
     * Sets the error message about the verification code.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setVerificationCodeError(int value) {
        if (value != myVerificationCodeErr) {
            myVerificationCodeErr = value;
            notifyPropertyChanged(BR.verificationCodeError);
        }
    }

    /**
     * Gets the verification code validator.
     *
     * @return The object.
     */
    @NonNull
    @Bindable
    public EditTextValidator getVerificationCodeValidator() {
        return myVerificationCodeValidator;
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        if (!myVerificationCodeValidator.validate()) {
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
        myVerificationCode = in.readString();
        myVerificationCodeErr = in.readInt();
        super.readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myVerificationCode);
        dest.writeInt(myVerificationCodeErr);
        super.writeToParcel(dest, flags);
    }
}
