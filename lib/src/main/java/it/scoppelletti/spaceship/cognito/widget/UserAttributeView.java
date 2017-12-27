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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.UserAttribute;
import it.scoppelletti.spaceship.cognito.data.UserAttributeViewModel;
import it.scoppelletti.spaceship.widget.CompoundControl;

/**
 * Widget for a user attribute.
 *
 * @since 1.0.0
 */
public final class UserAttributeView extends CompoundControl {
    private TextView myLabelView;
    private TextInputLayout myValueView;
    private UserAttributeValidator myValidator;

    /**
     * Constructor.
     *
     * @param ctx The context.
     */
    public UserAttributeView(@NonNull Context ctx) {
        super(ctx);
        init();
    }

    /**
     * Constructor.
     *
     * @param ctx   The context.
     * @param attrs The attributes. May be {@code null}.
     */
    public UserAttributeView(@NonNull Context ctx,
            @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        init();
    }

    /**
     * Creates the UI.
     */
    private void init() {
        View view;
        LayoutInflater inflater;

        inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(
                R.layout.it_scoppelletti_cognito_userattribute_view, this,
                false);
        addView(view);

        myLabelView = view.findViewById(R.id.lbl_value);
        myValueView = view.findViewById(R.id.txt_value);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
    }

    /**
     * Binds a user attribute model.
     *
     * @param model The model.
     */
    public void bind(@NonNull UserAttributeViewModel model) {
        int label;
        String err, key;
        EditText valueView;

        if (model == null) {
            throw new NullPointerException("Argument model is null.");
        }
        if (myValidator != null) {
            throw new IllegalStateException("Control already bound.");
        }

        key = model.getKey();
        label = getLabel(key);
        if (label < 0) {
            myLabelView.setText(key);
        } else {
            myLabelView.setText(label);
        }

        valueView = myValueView.getEditText();
        valueView.setInputType(getInputType(key));
        valueView.setText(model.getEditingValue());
        myValidator = new UserAttributeValidator(getContext(), model,
                myValueView);

        err = model.getError();
        if (TextUtils.isEmpty(err)) {
            myValueView.setErrorEnabled(false);
            myValueView.setError(null);
        } else {
            myValueView.setErrorEnabled(true);
            myValueView.setError(err);
        }
    }

    /**
     * Validates the model.
     *
     * @return Returns {@code true} if the model is valid, {@code false}
     *         otherwise.
     */
    public boolean validate() {
        return (myValidator == null || myValidator.validate());
    }

    /**
     * Gets the label of a user attribute.
     *
     * @param  key Key of the user attribute.
     * @return     The label as a string resource ID. May be {@code -1}.
     */
    private int getLabel(String key) {
        int label;

        switch (key) {
        case UserAttribute.ATTR_ADDRESS:
            label = R.string.it_scoppelletti_lbl_address;
            break;

        case UserAttribute.ATTR_BIRTHDATE:
            label = R.string.it_scoppelletti_lbl_birthdate;
            break;

        case UserAttribute.ATTR_EMAIL:
            label = R.string.it_scoppelletti_lbl_email;
            break;

        case UserAttribute.ATTR_GENDER:
            label = R.string.it_scoppelletti_lbl_gender;
            break;

        case UserAttribute.ATTR_LOCALE:
            label = R.string.it_scoppelletti_lbl_locale;
            break;

        case UserAttribute.ATTR_MIDDLENAME:
            label = R.string.it_scoppelletti_lbl_middleName;
            break;

        case UserAttribute.ATTR_NAME:
            label = R.string.it_scoppelletti_lbl_name;
            break;

        case UserAttribute.ATTR_NICKNAME:
            label = R.string.it_scoppelletti_lbl_nickname;
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            label = R.string.it_scoppelletti_lbl_phoneNumber;
            break;

        case UserAttribute.ATTR_PICTURE:
            label = R.string.it_scoppelletti_cognito_lbl_picture;
            break;

        case UserAttribute.ATTR_PREFEREEDUSERCODE:
            label = R.string.it_scoppelletti_cognito_lbl_preferredUserCode;
            break;

        case UserAttribute.ATTR_PROFILE:
            label = R.string.it_scoppelletti_cognito_lbl_profile;
            break;

        case UserAttribute.ATTR_SURNAME:
            label = R.string.it_scoppelletti_lbl_surname;
            break;

        case UserAttribute.ATTR_TIMEZONE:
            label = R.string.it_scoppelletti_lbl_timeZone;
            break;

        case UserAttribute.ATTR_USERCODE:
            label = R.string.it_scoppelletti_lbl_userCode;
            break;

        case UserAttribute.ATTR_WEBSITE:
            label = R.string.it_scoppelletti_lbl_website;
            break;

        default:
            // Use a plug-in for custom attributes
            label = -1;
            break;
        }

        return label;
    }

    /**
     * Gets the input type of a user attribute.
     *
     * @param  key Key of the user attribute.
     * @return     The input type.
     */
    private int getInputType(String key) {
        int inputType;

        switch (key) {
        case UserAttribute.ATTR_ADDRESS:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
            break;

        case UserAttribute.ATTR_BIRTHDATE:
            // Use a picker
            inputType = InputType.TYPE_CLASS_DATETIME |
                    InputType.TYPE_DATETIME_VARIATION_DATE;
            break;

        case UserAttribute.ATTR_EMAIL:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            break;

        case UserAttribute.ATTR_GENDER:
            // Use a select list
            inputType = InputType.TYPE_CLASS_TEXT;
            break;

        case UserAttribute.ATTR_LOCALE:
            // Use a select list
            inputType = InputType.TYPE_CLASS_TEXT;
            break;

        case UserAttribute.ATTR_MIDDLENAME:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS;
            break;

        case UserAttribute.ATTR_NAME:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS;
            break;

        case UserAttribute.ATTR_NICKNAME:
            inputType = InputType.TYPE_CLASS_TEXT;
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            inputType = InputType.TYPE_CLASS_PHONE;
            break;

        case UserAttribute.ATTR_PICTURE:
            // Use a picker
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_URI;
            break;

        case UserAttribute.ATTR_PREFEREEDUSERCODE:
            inputType = InputType.TYPE_CLASS_TEXT;
            break;

        case UserAttribute.ATTR_PROFILE:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_URI;
            break;

        case UserAttribute.ATTR_SURNAME:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS;
            break;

        case UserAttribute.ATTR_TIMEZONE:
            // Use a select list
            inputType = InputType.TYPE_CLASS_TEXT;
            break;

        case UserAttribute.ATTR_USERCODE:
            inputType = InputType.TYPE_CLASS_TEXT;
            break;

        case UserAttribute.ATTR_WEBSITE:
            inputType = InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_URI;
            break;

        default:
            // Use a plug-in for custom attributes
            inputType = InputType.TYPE_CLASS_TEXT;
            break;
        }

        return inputType;
    }
}
