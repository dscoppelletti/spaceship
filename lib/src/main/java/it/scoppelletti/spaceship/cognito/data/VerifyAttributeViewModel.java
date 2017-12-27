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
 * Verify attribute view-model.
 *
 * @since 1.0.0
 */
public final class VerifyAttributeViewModel extends BaseObservable implements
        Parcelable {

    /**
     * The {@code Parcelable} support.
     */
    public static final Creator<VerifyAttributeViewModel> CREATOR =
            new Creator<VerifyAttributeViewModel>() {

        @Override
        public VerifyAttributeViewModel createFromParcel(Parcel in) {
            return new VerifyAttributeViewModel(in);
        }

        @Override
        public VerifyAttributeViewModel[] newArray(int size) {
            return new VerifyAttributeViewModel[size];
        }
    };

    private final EditTextValidator myCheckCodeValidator;
    private String myCheckCode;
    private int myCheckCodeErr;

    /**
     * Sole constructor.
     */
    public VerifyAttributeViewModel() {
        super();

        myCheckCodeValidator = () -> {
            if (TextUtils.isEmpty(myCheckCode)) {
                setVerificationCodeError(R.string.it_scoppelletti_cognito_err_verificationCodeRequired);
                return false;
            }

            setVerificationCodeError(0);
            return true;
        };
    }

    /**
     * Constructor.
     *
     * @param in The input stream.
     */
    private VerifyAttributeViewModel(Parcel in) {
        this();

        myCheckCode = in.readString();
        myCheckCodeErr = in.readInt();
    }

    /**
     * Gets the verification code.
     *
     * @return The value. May be {@code null}.
     */
    @Bindable
    @Nullable
    public String getVerificationCode() {
        return myCheckCode;
    }

    /**
     * Sets the verification code.
     *
     * @param value A value. May be {@code null}.
     */
    public void setVerificationCode(@Nullable String value) {
        if (!TextUtils.equals(value, myCheckCode)) {
            notifyPropertyChanged(BR.verificationCode);
            myCheckCode = value;
        }

        myCheckCodeValidator.validate();
    }

    /**
     * Gets the error message about the verification code.
     *
     * @return The value as a string resource ID. May be {@code 0}.
     */
    @Bindable
    public int getVerificationCodeError() {
        return myCheckCodeErr;
    }

    /**
     * Sets the error message about the verification code.
     *
     * @param value A value as a string resource ID. May be {@code 0}.
     */
    public void setVerificationCodeError(int value) {
        if (value != myCheckCodeErr) {
            myCheckCodeErr = value;
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
        return myCheckCodeValidator;
    }

    /**
     * Validates this model.
     *
     * @return Returns {@code true} if this model is valid, {@code false}
     *         otherwise.
     */
    public boolean validate() {
        return myCheckCodeValidator.validate();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myCheckCode);
        dest.writeInt(myCheckCodeErr);
    }
}
