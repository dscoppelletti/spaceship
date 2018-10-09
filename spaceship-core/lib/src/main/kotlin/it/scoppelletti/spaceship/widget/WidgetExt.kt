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

package it.scoppelletti.spaceship.widget

import android.text.InputType
import androidx.annotation.UiThread
import com.google.android.material.textfield.TextInputEditText

/**
 * Returns whether this `TextInputEditText` widget is enabled.
 *
 * @receiver Widget.
 * @return   Returns `true` if this widget is enabled, `false` otherwise.
 * @since    1.0.0
 */
@UiThread
public fun TextInputEditText.isWidgetEnabled(): Boolean =
        this.isEnabled && this.inputType != InputType.TYPE_NULL
