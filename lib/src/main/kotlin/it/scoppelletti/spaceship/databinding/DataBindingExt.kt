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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.databinding

import android.text.InputType
import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.scoppelletti.spaceship.content.res.ResourcesExt
import it.scoppelletti.spaceship.widget.isWidgetEnabled

/**
 * Data binding operations.
 *
 * @since 1.0.0
 */
public object DataBindingExt {

    /**
     * Indicates whether a widget is enabled or not.
     */
    public const val ATTR_ENABLED = "it_scoppelletti_enabled"

    /**
     * Error message as a string resource ID.
     */
    public const val ATTR_ERROR = "it_scoppelletti_error"

    /**
     * Input type for a text widget.
     */
    public const val ATTR_INPUTTYPE = "it_scoppelletti_inputType"

    /**
     * Validator for a widget.
     */
    public const val ATTR_VALIDATOR = "it_scoppelletti_validator"
}

/**
 * Enables or disables this widget.
 *
 * If you want to make a `TextInputEditText` widget disabled, you have to set
 * the `inputType` property to `InputType.TYPE_NULL`, but, if you want to
 * reenable the widget, you should restore the `inputType` property to the value
 * according to the type and variant of data that the widget contains, thus the
 * code must know these details whereas it is preferable to maintain these in
 * the XML layout.
 *
 * In the XML layout, you can use the `it_scoppelletti_enabled` attribute to set
 * whether the widget has to be enabled or disabled, and the
 * `it_scoppelletti_inputType` attribute to set the type and variant of data the
 * user can enter in the widget when it is enabled.
 *
 * @receiver          Widget.
 * @param   enabled   Whether the widget is enabled or not.
 * @param   inputType Type and variant of data.
 * @since             1.0.0
 */
@BindingAdapter(DataBindingExt.ATTR_ENABLED, DataBindingExt.ATTR_INPUTTYPE)
public fun TextInputEditText.setWidgetEnabled(
        enabled: Boolean,
        inputType: Int
) {
    if (enabled) {
        this.inputType = inputType
    } else {
        this.inputType = InputType.TYPE_NULL
        this.keyListener = null
        // - LG-H320, Android 5.0.1
        // If I set the android:textIsSelectable attribute to "true"  (through
        // by the setTextIsSelectable method or by XML), the soft keyboard
        // doesn't appear anymore, not even if I use the
        // setShowSoftInputOnFocus method (that anyway requires API level 21+).
    }
}

/**
 * Sets a validator for a `TextInputEditText` widget to be called when the
 * widget looses the focus.
 *
 * @receiver           Widget.
 * @param    validator Validator.
 * @since              1.0.0
 */
@BindingAdapter(DataBindingExt.ATTR_VALIDATOR)
public fun TextInputEditText.setWidgetValidator(validator: (() -> Boolean)?) {
    this.onFocusChangeListener = if (validator == null) null else
        View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && v is TextInputEditText && v.isWidgetEnabled()) {
                validator()
            }
        }
}

/**
 * Enables or disables this widget.
 *
 * If you disable the widget within a `TextInputLayout` decorator, you should
 * also disable the hint animation of the `TextInputLayout` widget. The
 * `it_scoppelletti_enabled` attribute hides this detail.
 *
 * @receiver         Widget.
 * @param    enabled Whether this widget is enabled or not.
 * @since            1.0.0
 */
@BindingAdapter(DataBindingExt.ATTR_ENABLED)
public fun TextInputLayout.setWidgetEnabled(enabled: Boolean) {
    this.isHintAnimationEnabled = enabled
}

/**
 * Sets an error message that will be displayed below the widget within this
 * decorator.
 *
 * The `it_scoppelletti_error` attribute controls both attributes `error` and
 * `errorEnabled`.
 *
 * @receiver         Decorator.
 * @param    errorId Error message as a string resource ID. If
 *                   `ResourcesExt.ID_NULL`, the error message will be cleared.
 * @since            1.0.0
 */
@BindingAdapter(DataBindingExt.ATTR_ERROR)
public fun TextInputLayout.setWidgetError(errorId: Int) {
    if (errorId == ResourcesExt.ID_NULL) {
        this.isErrorEnabled = false
        this.error = null
    } else {
        this.isErrorEnabled = true
        this.error = this.context.getString(errorId)
    }
}

/**
 * Sets an error message that will be displayed below the widget within this
 * decorator.
 *
 * The `it_scoppelletti_error` attribute controls both attributes `error` and
 * `errorEnabled`.
 *
 * @receiver              Decorator.
 * @param    errorMessage Error message as a string resource ID. If `null`, the
 *                        error message will be cleared.
 * @since                 1.0.0
 */
@BindingAdapter(DataBindingExt.ATTR_ERROR)
public fun TextInputLayout.setWidgetError(errorMessage: String?) {
    if (errorMessage.isNullOrBlank()) {
        this.isErrorEnabled = false
        this.error = null
    } else {
        this.isErrorEnabled = true
        this.error = errorMessage
    }
}
