/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.app.ExceptionDialogFragment

/**
 * `ViewModel` used by an activity to pass an exception to an
 * [ExceptionDialogFragment] fragment.
 *
 * @since 1.0.0
 *
 * @property ex Exception.
 *
 * @constructor Sole constructor.
 */
public class ExceptionViewModel : ViewModel() {

    public var ex: Throwable? = null
}
