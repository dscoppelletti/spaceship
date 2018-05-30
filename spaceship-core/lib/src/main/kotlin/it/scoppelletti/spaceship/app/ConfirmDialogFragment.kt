/*
 * Copyright (C) 2013-2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.annotation.UiThread
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDialogFragment

/**
 * Confirmation dialog.
 *
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
@UiThread
public class ConfirmDialogFragment : AppCompatDialogFragment() {
    // - Genymotion 2.12.1
    // Negative button has a gray background, but only if the dialog is opened
    // in the method onBackPressed!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val msg: String?
        val args: Bundle
        val builder: AlertDialog.Builder

        args = arguments!!
        msg = args.getString(ConfirmDialogFragment.PROP_MSG)

        builder = AlertDialog.Builder(requireActivity())
                .setTitle(args.getInt(ConfirmDialogFragment.PROP_TITLEID))
                .setPositiveButton(
                        args.getInt(ConfirmDialogFragment.PROP_POSITIVEID,
                                android.R.string.ok),
                        ::onDialogResult)
                .setNegativeButton(
                        args.getInt(ConfirmDialogFragment.PROP_NEGATIVEID,
                                android.R.string.cancel),
                        ::onDialogResult)
                .apply {
                    if (!msg.isNullOrBlank()) {
                        setMessage(msg)
                    } else {
                        setMessage(
                                args.getInt(ConfirmDialogFragment.PROP_MSGID))
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
     *              (`DialogInterface.BUTTON_POSITIVE` or
     *              `DialogInterface.BUTTON_NEGATIVE`).
     */
    private fun onDialogResult(
            @Suppress("UNUSED_PARAMETER") dialog: DialogInterface?,
            which: Int
    ) {
        val dialogId: Int
        val args: Bundle
        val activity: FragmentActivity

        args = arguments!!
        dialogId = args.getInt(ConfirmDialogFragment.PROP_DLGID, -1)
        activity = requireActivity()

        if (dialogId > 0 && activity is OnDialogResultListener) {
            activity.onDialogResult(dialogId, which)
        }
    }

    companion object {

        /**
         * The fragment tag.
         */
        public const val TAG: String = AppExt.TAG_CONFIRMDIALOG

        private const val PROP_MSG: String = "1"
        private const val PROP_MSGID: String = "2"
        private const val PROP_TITLEID: String = "3"
        private const val PROP_DLGID: String = "4"
        private const val PROP_POSITIVEID: String = "5"
        private const val PROP_NEGATIVEID: String = "6"

        /**
         * Shows a confirmation dialog.
         *
         * @param activity                 The activity.
         * @param messageId                The message as a string resource ID.
         * @param messageArguments         The arguments to build the message.
         * @param titleId                  The title as a string resource ID.
         * @param dialogId                 The dialog ID.
         * @param affermativeActionTextId  The affermative action text as a
         *                                 string resource ID.
         * @param dismissiveActionTextId   The dismissive action text as a
         *                                 string resource ID.
         */
        public fun show(
                activity: FragmentActivity,
                @StringRes messageId: Int,
                messageArguments: Array<out Any>? = null,
                @StringRes titleId: Int,
                dialogId: Int = -1,
                @StringRes affermativeActionTextId: Int = android.R.string.ok,
                @StringRes dismissiveActionTextId: Int =
                        android.R.string.cancel
        ) {
            val args: Bundle
            val fragment: ConfirmDialogFragment

            args = Bundle()
            if (messageArguments == null) {
                args.putInt(ConfirmDialogFragment.PROP_MSGID, messageId)
            } else {
                args.putString(ConfirmDialogFragment.PROP_MSG,
                        activity.getString(messageId, *messageArguments))
            }

            args.putInt(ConfirmDialogFragment.PROP_TITLEID, titleId)

            if (affermativeActionTextId != android.R.string.ok) {
                args.putInt(ConfirmDialogFragment.PROP_POSITIVEID,
                        affermativeActionTextId)
            }

            if (dismissiveActionTextId != android.R.string.cancel) {
                args.putInt(ConfirmDialogFragment.PROP_NEGATIVEID,
                        dismissiveActionTextId)
            }

            if (dialogId > 0) {
                args.putInt(ConfirmDialogFragment.PROP_DLGID, dialogId)
            }

            fragment = ConfirmDialogFragment()
            fragment.arguments = args
            fragment.show(activity.supportFragmentManager,
                    ConfirmDialogFragment.TAG)
        }
    }
}