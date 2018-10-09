/*
 * Copyright (C) 2015-2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.CoreExt
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
        val resId: Int
        val args: Bundle
        val builder: AlertDialog.Builder
        var msg: String?

        args = arguments!!
        builder = AlertDialog.Builder(requireActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, ::onDialogResult)

        msg = args.getString(ExceptionDialogFragment.PROP_MSG)
        if (msg.isNullOrBlank()) {
            builder.setMessage(args.getInt(ExceptionDialogFragment.PROP_MSGID))
        } else {
            builder.setMessage(msg)
        }

        msg = args.getString(ExceptionDialogFragment.PROP_TITLE)
        if (msg.isNullOrBlank()) {
            resId = args.getInt(ExceptionDialogFragment.PROP_TITLEID,
                    android.R.string.dialog_alert_title)
            builder.setTitle(resId)
        } else {
            builder.setTitle(msg)
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
     * @param dialog Dialog that received the click.
     * @param which  ID of the button that was clicked
     *               (`DialogInterface.BUTTON_NEGATIVE`).
     */
    private fun onDialogResult(
            @Suppress("UNUSED_PARAMETER") dialog: DialogInterface?,
            which: Int
    ) {
        val dialogTag: String?
        val activity: FragmentActivity

        dialogTag = tag
        activity = requireActivity()

        if (dialogTag != null && activity is OnDialogResultListener) {
            activity.onDialogResult(dialogTag, which)
        }
    }

    public companion object {

        /**
         * Fragment tag.
         */
        public const val TAG: String = CoreExt.TAG_EXCEPTIONDIALOG

        private const val PROP_MSG: String = "1"
        private const val PROP_MSGID: String = "2"
        private const val PROP_TITLE: String = "3"
        private const val PROP_TITLEID: String = "4"
    }

    /**
     * Builds an `ExceptionDialogFragment` fragment.
     *
     * @since 1.0.0
     *
     * @property tag Fragment tag.
     */
    @ExceptionDialogFragment.Dsl
    public class Builder internal constructor(
            private val activity: FragmentActivity,
            private val ex: Throwable
    ) {
        public var tag: String = ExceptionDialogFragment.TAG

        internal fun show() {
            val args: Bundle

            args = Bundle()

            if (ex is ApplicationException) {
                if (ex.messageBuilder.isSimple) {
                    args.putInt(ExceptionDialogFragment.PROP_MSGID,
                            ex.messageBuilder.messageId)
                } else {
                    args.putString(ExceptionDialogFragment.PROP_MSG,
                            ex.messageBuilder.build(activity.resources))
                }

                if (ex.titleBuilder.isSimple) {
                    args.putInt(ExceptionDialogFragment.PROP_TITLEID,
                            ex.titleBuilder.messageId)
                } else {
                    args.putString(ExceptionDialogFragment.PROP_TITLE,
                            ex.titleBuilder.build(activity.resources))
                }
            } else {
                args.putString(ExceptionDialogFragment.PROP_MSG,
                        ex.toMessage())
            }

            ExceptionDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(activity.supportFragmentManager, tag)
        }
    }

    /**
     * Marks the `ExceptionDialogFragment` DSL's objects.
     *
     * @since 1.0.0
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Shows an exception dialog.
 *
 * @receiver      Activity.
 * @param    ex   Exception.
 * @param    init Initialization block.
 * @since         1.0.0
 */
public fun FragmentActivity.showExceptionDialog(
        ex: Throwable,
        init: ExceptionDialogFragment.Builder.() -> Unit = { }
) = ExceptionDialogFragment.Builder(this, ex).apply(init).show()
