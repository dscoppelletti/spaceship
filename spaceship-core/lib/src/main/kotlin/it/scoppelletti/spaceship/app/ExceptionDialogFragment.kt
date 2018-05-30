/*
 * Copyright (C) 2015-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.toMessage

/**
 * Exception dialog.
 *
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
@UiThread
public class ExceptionDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val msg: String?
        val args: Bundle
        val builder: AlertDialog.Builder

        args = arguments!!
        msg = args.getString(ExceptionDialogFragment.PROP_MSG)

        builder = AlertDialog.Builder(requireActivity())
                .setTitle(args.getInt(ExceptionDialogFragment.PROP_TITLEID,
                        android.R.string.dialog_alert_title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, ::onDialogResult)
                .apply {
                    if (!msg.isNullOrBlank()) {
                        setMessage(msg)
                    } else {
                        setMessage(
                                args.getInt(ExceptionDialogFragment.PROP_MSGID))
                    }
                }

        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        onDialogResult(null, DialogInterface.BUTTON_NEGATIVE)
    }

    /**
     * Handles the result of this dialog.
     *
     * @param dialog The dialog that received the click.
     * @param which  ID of the button that was clicked
     *               (`DialogInterface.BUTTON_NEGATIVE`).
     */
    private fun onDialogResult(
            @Suppress("UNUSED_PARAMETER") dialog: DialogInterface?,
            @Suppress("UNUSED_PARAMETER") which: Int
    ) {
        val dialogId: Int
        val args: Bundle
        val activity: FragmentActivity

        args = arguments!!
        dialogId = args.getInt(ExceptionDialogFragment.PROP_DLGID, -1)
        activity = requireActivity()

        if (dialogId > 0 && activity is OnDialogResultListener) {
            activity.onDialogResult(dialogId, DialogInterface.BUTTON_NEGATIVE)
        }
    }

    companion object {

        /**
         * The fragment tag.
         */
        public const val TAG: String = AppExt.TAG_EXCEPTIONDIALOG

        private const val PROP_MSG: String = "1"
        private const val PROP_MSGID: String = "2"
        private const val PROP_TITLEID: String = "3"
        private const val PROP_DLGID: String = "4"

        /**
         * Shows an exception dialog.
         *
         * @param activity The activity.
         * @param ex       The exception.
         * @param dialogId The dialog ID.
         */
        public fun show(
                activity: FragmentActivity,
                ex: Throwable,
                dialogId: Int = -1
        ) {
            val args: Bundle
            val fragment: ExceptionDialogFragment

            args = Bundle()
            if (ex is ApplicationException) {
                if (ex.messageArguments == null) {
                    args.putInt(ExceptionDialogFragment.PROP_MSGID,
                            ex.messageId)
                } else {
                    args.putString(ExceptionDialogFragment.PROP_MSG,
                            activity.getString(ex.messageId,
                                    *ex.messageArguments))
                }

                if (ex.titleId != android.R.string.dialog_alert_title) {
                    args.putInt(ExceptionDialogFragment.PROP_TITLEID,
                            ex.titleId)
                }
            } else {
                args.putString(ExceptionDialogFragment.PROP_MSG,
                        ex.toMessage())
            }

            if (dialogId > 0) {
                args.putInt(ExceptionDialogFragment.PROP_DLGID, dialogId)
            }

            fragment = ExceptionDialogFragment()
            fragment.arguments = args
            fragment.show(activity.supportFragmentManager,
                    ExceptionDialogFragment.TAG)
        }
    }
}

