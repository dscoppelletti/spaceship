/*
 * Copyright (C) 2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.databinding;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.View;
import it.scoppelletti.spaceship.widget.EditTextValidator;

/**
 * Data binding adapter for {@code TextInputLayout} and
 * {@code TextInputEditText} widgets.
 *
 * @since 1.0.0
 */
@UiThread
public final class TextInputLayoutAdapter {

    /**
     * Private constructor for static class.
     */
    private TextInputLayoutAdapter() {
    }

    /**
     * Returns whether a {@code TextInputEditText} widget is enabled.
     *
     * @param  control The widget.
     * @return         Returns {@code true} if the widget is enabled,
     *                 {@code false} otherwise.
     * @see    #setEnabled(android.support.design.widget.TextInputEditText, boolean, int)
     */
    public static boolean isTextInputEditTextEnabled(
            @NonNull TextInputEditText control) {
        if (control == null) {
            throw new NullPointerException("Argument control is null.");
        }

        return (control.getInputType() != InputType.TYPE_NULL);
    }

    /**
     * Enables or disables a widget.
     *
     * <p>If you want to make a {@code TextInputEditText} widget disabled, you
     * have to set the {@code inputType} property to
     * {@code InputType.TYPE_NULL}, but, if you want to reenable the widget,
     * you should restore the {@code inputType} property to the value according
     * to the type and variant of data that the widget contains, thus the code
     * must know these details whereas it is preferable to maintain these in the
     * XML layout.<br />
     * In the XML layout, you can use the {@code it_scoppelletti_enabled}
     * attribute to set whether the widget has to be enabled or disabled, and
     * the {@code it_scoppelletti_inputType} attribute to set the type and
     * variant of data the user can enter in the widget when it is enabled.</p>
     *
     * @param control    The widget.
     * @param enabled    Whether the widget is enabled or not.
     * @param inputType  The type and variant of data.
     * @see   #isTextInputEditTextEnabled(android.support.design.widget.TextInputEditText)
     */
    @BindingAdapter({ DataBindingExt.ATTR_ENABLED,
            DataBindingExt.ATTR_INPUTTYPE })
    public static void setEnabled(@NonNull TextInputEditText control,
            boolean enabled, int inputType) {
        if (control == null) {
            throw new NullPointerException("Argument control is null.");
        }

        if (enabled) {
            control.setInputType(inputType);
        } else {
            control.setInputType(InputType.TYPE_NULL);
            control.setKeyListener(null);

            // - LG-H320, Android 5.0.1
            // If I set the android:textIsSelectable attribute to "true"
            // (through by the setTextIsSelectable method or by XML), the soft
            // keyboard doesn't appear anymore, not even if I use the
            // setShowSoftInputOnFocus method (that anyway requires API level
            // 21+).
        }
    }

    /**
     * Sets a validator for a {@code TextInputEditText} widget to be called
     * when the widget loose the focus.
     *
     * @param control   The widget.
     * @param validator The validator. May be {@code null}.
     */
    @BindingAdapter(DataBindingExt.ATTR_VALIDATOR)
    public static void setValidator(@NonNull TextInputEditText control,
            @Nullable EditTextValidator validator) {
        View.OnFocusChangeListener listener;

        if (control == null) {
            throw new NullPointerException("Argument control is null.");
        }

        listener = (validator == null) ? null :
                new OnFocusChangeListener(validator);
        control.setOnFocusChangeListener(listener);
    }

    /**
     * Enables or disables a widget.
     *
     * <p>If you disable the widget within a {@code TextInputLayout} decorator,
     * you should also disable the hint animation of the {@code TextInputLayout}
     * widget.<br />
     * The {@code it_scoppelletti_enabled} attribute hides this detail.</p>
     *
     * @param control The widget.
     * @param enabled Whether the widget is enabled or not.
     */
    @BindingAdapter(DataBindingExt.ATTR_ENABLED)
    public static void setTextInputLayoutEnabled(
            @NonNull TextInputLayout control, boolean enabled) {
        if (control == null) {
            throw new NullPointerException("Argument control is null.");
        }

        control.setHintAnimationEnabled(enabled);
    }

    /**
     * Sets an error message that will be displayed below the widget within a
     * {@code TextInputLayout} decorator.
     *
     * <p>The {@code it_scoppelletti_error} attribute controls both attributes
     * {@code error} and {@code errorEnabled}.</p>
     *
     * @param control A widget.
     * @param errorId The error message as a string resource ID. If not greater
     *                than {@code 0}, the error message will be cleared.
     */
    @BindingAdapter(DataBindingExt.ATTR_ERROR)
    public static void setError(@NonNull TextInputLayout control, int errorId) {
        if (control == null) {
            throw new NullPointerException("Argument control is null.");
        }

        if (errorId > 0) {
            control.setErrorEnabled(true);
            control.setError(control.getContext().getString(errorId));
        } else {
            control.setErrorEnabled(false);
            control.setError(null);
        }
    }

    /**
     * Listener for the validation of a {@code TextInputEditText} widget.
     */
    private static final class OnFocusChangeListener implements
            View.OnFocusChangeListener {
        private final EditTextValidator myValidator;

        /**
         * Constructor.
         *
         * @param validator The validator.
         */
        OnFocusChangeListener(EditTextValidator validator) {
            myValidator = validator;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            TextInputEditText control;

            if (hasFocus || !(v instanceof TextInputEditText)) {
                return;
            }

            control = (TextInputEditText) v;
            if (TextInputLayoutAdapter.isTextInputEditTextEnabled(control)) {
                myValidator.validate();
            }
        }
    }
}
