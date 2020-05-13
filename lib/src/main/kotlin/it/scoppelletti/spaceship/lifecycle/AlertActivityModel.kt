/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.app.AlertDialogFragment
import it.scoppelletti.spaceship.i18n.MessageSpec

/**
 * `ViewModel` used by an activity to pass a message to an [AlertDialogFragment]
 * fragment.
 *
 * @since 1.0.0
 *
 * @property message Message.
 */
public class AlertActivityModel : ViewModel() {

    public var message: MessageSpec? = null
}

