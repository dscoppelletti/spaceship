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

package it.scoppelletti.spaceship.i18n

import android.content.res.Resources
import it.scoppelletti.spaceship.R
import javax.inject.Inject

/**
 * Default implementation of the `UIMessages` interface.
 *
 * @since 1.0.0
 */
public class DefaultAppMessages @Inject constructor(
        private val resources: Resources
) : AppMessages {

    override fun errorClipboardNotSupported(): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_err_clipoardNotSupported)

    override fun errorDateFormat(pattern: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                R.string.it_scoppelletti_err_dateFormat, pattern)

    override fun errorPackageNotFound(name: String): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_err_packageNotFound, name)

    override fun errorStartActivity(): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_err_startActivity)

    override fun errorTimeFormat(pattern: String): MessageSpec =
        AndroidResourceMessageSpec.of(resources,
            R.string.it_scoppelletti_err_timeFormat, pattern)

    override fun promptSaveChanges(): MessageSpec =
            AndroidResourceMessageSpec.of(resources,
                    R.string.it_scoppelletti_prompt_saveChanges)
}
