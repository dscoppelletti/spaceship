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

package it.scoppelletti.spaceship.preference

import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View

/**
 * Editor dialog for a custom setting [CustomDialogPreference].
 *
 * @since 1.0.0
 */
@UiThread
public class CustomPreferenceDialogFragment : PreferenceDialogFragmentCompat() {

    override fun onBindDialogView(view: View?) {
        val pref: CustomDialogPreference?

        super.onBindDialogView(view)

        if (view != null) {
            pref = preference as? CustomDialogPreference
            pref?.doBindDialogView(view)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        val pref: CustomDialogPreference?

        pref = preference as? CustomDialogPreference
        pref?.doDialogClosed(positiveResult)
    }

    companion object {
        public fun newInstance(key: String): CustomPreferenceDialogFragment {
            val args: Bundle

            args = Bundle().apply {
                putString(PreferenceDialogFragmentCompat.ARG_KEY, key)
            }

            return CustomPreferenceDialogFragment().apply {
                arguments = args
            }
        }
    }
}
