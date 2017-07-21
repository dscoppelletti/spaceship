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

package it.scoppelletti.spaceship.cognito.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.UserAttribute;
import it.scoppelletti.spaceship.cognito.data.UserAttributeForm;

/**
 * Validator for a user attribute form.
 */
final class UserAttributeValidator implements TextWatcher,
        View.OnFocusChangeListener {
    private final Context myCtx;
    private final UserAttributeForm myForm;
    private final TextInputLayout myControl;

    /**
     * Constructor.
     *
     * @param ctx     The context.
     * @param form    The form.
     * @param control The control.
     */
    UserAttributeValidator(@NonNull Context ctx,
            @NonNull UserAttributeForm form, @NonNull TextInputLayout control) {
        EditText view;

        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }
        if (form == null) {
            throw new NullPointerException("Argument data is null.");
        }
        if (control == null) {
            throw new NullPointerException("Argument control is null.");
        }

        myCtx = ctx;
        myForm = form;
        myControl = control;
        view = myControl.getEditText();
        view.setOnFocusChangeListener(this);
        view.addTextChangedListener(this);
    }

    /**
     * Validates the form.
     *
     * @return Return {@code true} if the form is valid, {@code false}
     *         otherwise.
     */
    boolean validate() {
        return validate(myControl.getEditText().getText());
    }

    /**
     * Validates a value.
     *
     * @param  value The value to validate.
     * @return       Return {@code true} if the value is valid, {@code false}
     *               otherwise.
     */
    private boolean validate(CharSequence value) {
        String err;

        if (myForm.isRequired() && TextUtils.isEmpty(value)) {
            myControl.setErrorEnabled(true);
            err = getRequiredError(myForm.getKey());
            myControl.setError(err);
            myForm.setError(err);
            return false;
        }

        // Implement specific validation for ATTR_BIRTHDATE, ATTR_EMAIL,
        // ATTR_WEBSITE.
        // Use a plug-in to validate custom attributes.

        myControl.setErrorEnabled(false);
        myControl.setError(null);
        myForm.setError(null);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
            int count) {
        validate(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        myForm.setEditingValue(s.toString());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            validate();
        }
    }

    /**
     * Gets the error message for missing a required value.
     *
     * @param  key Key of the user attribute.
     * @return     The message.
     */
    private String getRequiredError(String key) {
        int err;

        switch (key) {
        case UserAttribute.ATTR_ADDRESS:
            err = R.string.it_scoppelletti_err_addressRequired;
            break;

        case UserAttribute.ATTR_BIRTHDATE:
            err = R.string.it_scoppelletti_err_birthdateRequired;
            break;

        case UserAttribute.ATTR_EMAIL:
            err = R.string.it_scoppelletti_err_emailRequired;
            break;

        case UserAttribute.ATTR_GENDER:
            err = R.string.it_scoppelletti_err_genderRequired;
            break;

        case UserAttribute.ATTR_LOCALE:
            err = R.string.it_scoppelletti_err_localeRequired;
            break;

        case UserAttribute.ATTR_MIDDLENAME:
            err = R.string.it_scoppelletti_err_middleNameRequired;
            break;

        case UserAttribute.ATTR_NAME:
            err = R.string.it_scoppelletti_err_nameRequired;
            break;

        case UserAttribute.ATTR_NICKNAME:
            err = R.string.it_scoppelletti_err_nicknameRequired;
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            err = R.string.it_scoppelletti_err_phoneNumberRequired;
            break;

        case UserAttribute.ATTR_PICTURE:
            err = R.string.it_scoppelletti_cognito_err_pictureRequired;
            break;

        case UserAttribute.ATTR_PREFEREEDUSERCODE:
            err = R.string.it_scoppelletti_cognito_err_preferredUserCodeRequired;
            break;

        case UserAttribute.ATTR_PROFILE:
            err = R.string.it_scoppelletti_cognito_err_profileRequired;
            break;

        case UserAttribute.ATTR_SURNAME:
            err = R.string.it_scoppelletti_err_surnameRequired;
            break;

        case UserAttribute.ATTR_TIMEZONE:
            err = R.string.it_scoppelletti_err_timeZoneRequired;
            break;

        case UserAttribute.ATTR_USERCODE:
            err = R.string.it_scoppelletti_err_userCodeRequired;
            break;

        case UserAttribute.ATTR_WEBSITE:
            err = R.string.it_scoppelletti_err_websiteRequired;
            break;

        default:
            // Use a plug-in for custom attributes
            return myCtx.getString(
                    R.string.it_scoppelletti_cognito_err_userAttributeRequired,
                    key);
        }

        return myCtx.getString(err);
    }
}
