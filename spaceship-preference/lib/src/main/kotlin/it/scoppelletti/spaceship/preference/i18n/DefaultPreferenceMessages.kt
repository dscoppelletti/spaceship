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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.preference.i18n

import android.content.res.Resources
import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.preference.R
import javax.inject.Inject

/**
 * Default implementation of the `PreferenceMessages` interface.
 *
 * @since 1.0.0
 */
public class DefaultPreferenceMessages @Inject constructor(
        private val resources: Resources
) : PreferenceMessages{

    override fun errorCreditFailed(): MessageSpec =
            AndroidResourceMessageSpec(resources,
                    R.string.it_scoppelletti_pref_err_creditFailed)
}
