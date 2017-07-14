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

package it.scoppelletti.spaceship.databinding;

/**
 * Operations on data binding.
 *
 * @since 1.0.0
 */
public final class DataBindingExt {

    /**
     * Indicates whether a widget is enabled or not.
     */
    public static final String ATTR_ENABLED = "it_scoppelletti_enabled";

    /**
     * The error message as a string resource ID
     */
    public static final String ATTR_ERROR = "it_scoppelletti_error";

    /**
     * The input type for a text widget.
     */
    public static final String ATTR_INPUTTYPE = "it_scoppelletti_inputType";

    /**
     * The validator for a widget.
     */
    public static final String ATTR_VALIDATOR = "it_scoppelletti_validator";

    /**
     * Private constructor for static class.
     */
    private DataBindingExt() {
    }
}
