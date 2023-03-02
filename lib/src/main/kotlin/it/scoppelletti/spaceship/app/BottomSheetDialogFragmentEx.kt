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

package it.scoppelletti.spaceship.app

import android.content.DialogInterface
import androidx.annotation.UiThread
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Extended `BottomSheetDialogFragment` fragment.
 *
 * @since 1.0.0
 */
@UiThread
public abstract class BottomSheetDialogFragmentEx :
        BottomSheetDialogFragment() {

    /**
     * Dismisses the dialog.
     *
     * @param which ID of the button that was clicked (i.e.
     *              `DialogInterface.BUTTON_NEGATIVE`)
     */
    protected fun onItemClick(which: Int) {
        onDialogResult(dialog, which)
        dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDialogResult(dialog, DialogInterface.BUTTON_NEGATIVE)
    }

    /**
     * Handles the result of this dialog.
     *
     * @param dialog Dialog that received the click.
     * @param which  ID of the button that was clicked (i.e.
     *               `DialogInterface.BUTTON_NEGATIVE`).
     */
    private fun onDialogResult(
            @Suppress("UNUSED_PARAMETER") dialog: DialogInterface?,
            which: Int
    ) {
        tag?.let { dialogTag ->
            setFragmentResult(dialogTag, bundleOf(PROP_RESULT to which))
        }
    }

    public companion object {

        /**
         * Fragment tag.
         */
        public const val TAG: String = AppExt.TAG_BOTTOMSHEETDIALOG

        /**
         * Property containing the ID of the button that was clicked (ex.
         * `DialogInterface.BUTTON_POSITIVE`) or the position of the item
         * clicked.
         */
        public const val PROP_RESULT: String = AppExt.PROP_RESULT
    }
}
