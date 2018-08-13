/*
 * Copyright (C) 2013-2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.annotation.UiThread
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.MessageBuilder

/**
 * Alert dialog.
 *
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
@UiThread
public class AlertDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val msg: String?
        val args: Bundle
        val builder: AlertDialog.Builder
        var resId: Int

        args = arguments!!
        builder = AlertDialog.Builder(requireActivity())

        msg = args.getString(AlertDialogFragment.PROP_MSG)
        if (msg.isNullOrBlank()) {
            builder.setMessage(args.getInt(AlertDialogFragment.PROP_MSGID))
        } else {
            builder.setMessage(msg)
        }

        resId = args.getInt(AlertDialogFragment.PROP_TITLEID,
                android.R.string.dialog_alert_title)
        builder.setTitle(resId)

        resId = args.getInt(AlertDialogFragment.PROP_POSITIVEID,
                android.R.string.ok)
        builder.setPositiveButton(resId, ::onDialogResult)

        resId = args.getInt(AlertDialogFragment.PROP_NEGATIVEID,
                android.R.string.cancel)
        builder.setNegativeButton(resId, ::onDialogResult)

        resId = args.getInt(AlertDialogFragment.PROP_NEUTRALID)
        if (resId > 0) {
            builder.setNeutralButton(resId, ::onDialogResult)
        }

        resId = args.getInt(AlertDialogFragment.PROP_ICONID)
        if (resId > 0) {
            builder.setIcon(resId)
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
     *              `DialogInterface.BUTTON_NEGATIVE` or
     *              `DialogInterface.BUTTON_NEUTRAL`).
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
         * The fragment tag.
         */
        public const val TAG: String = CoreExt.TAG_ALERTDIALOG

        private const val PROP_MSG: String = "1"
        private const val PROP_MSGID: String = "2"
        private const val PROP_TITLEID: String = "3"
        private const val PROP_POSITIVEID: String = "4"
        private const val PROP_NEGATIVEID: String = "5"
        private const val PROP_NEUTRALID: String = "6"
        private const val PROP_ICONID: String = "7"
    }

    /**
     * Builds an `AlertDialogFragment` fragment.
     *
     * @since                         1.0.0
     * @property titleId              The title as a string resource ID.
     * @property positiveActionTextId The positive action text as a string
     *                                resource ID.
     * @property negativeActionTextId The negative action text as a string
     *                                resource ID.
     * @property neutralActionTextId  The neutral action text as a string
     *                                resource ID.
     * @property iconId               The icon as a `Drawable` resource ID.
     */
    @MessageBuilder.Dsl
    @AlertDialogFragment.Dsl
    public class Builder internal constructor(
            private val activity: FragmentActivity
    ) {

        @StringRes public var titleId: Int = android.R.string.dialog_alert_title
        @StringRes public var positiveActionTextId: Int = android.R.string.ok
        @StringRes public var negativeActionTextId: Int =
                android.R.string.cancel
        @StringRes public var neutralActionTextId: Int = 0
        @DrawableRes public var iconId: Int = 0
        public var tag: String = AlertDialogFragment.TAG
        private var messageBuilder: MessageBuilder? = null

        /**
         * Defines the `MessageBuilder` object.
         *
         * @param  messageId The message as a string resource ID.
         * @param  init      The initializiation block.
         * @return           The new object.
         */
        public fun message(
                @StringRes messageId: Int,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            messageBuilder = MessageBuilder.make(messageId, init)
            return messageBuilder!!
        }

        internal fun show() {
            val args: Bundle

            if (messageBuilder == null) {
                throw NullPointerException("Missing the MessageBuilder object.")
            }

            args = Bundle()
            messageBuilder?.let { msg ->
                if (msg.isSimple) {
                    args.putInt(AlertDialogFragment.PROP_MSGID, msg.messageId)
                } else {
                    args.putString(AlertDialogFragment.PROP_MSG,
                            msg.build(activity.resources))
                }
            }

            if (titleId != android.R.string.dialog_alert_title) {
                args.putInt(AlertDialogFragment.PROP_TITLEID, titleId)
            }
            if (positiveActionTextId != android.R.string.ok) {
                args.putInt(AlertDialogFragment.PROP_POSITIVEID,
                        positiveActionTextId)
            }
            if (negativeActionTextId != android.R.string.cancel) {
                args.putInt(AlertDialogFragment.PROP_NEGATIVEID,
                        negativeActionTextId)
            }
            if (neutralActionTextId > 0) {
                args.putInt(AlertDialogFragment.PROP_NEUTRALID,
                        neutralActionTextId)
            }
            if (iconId > 0) {
                args.putInt(AlertDialogFragment.PROP_ICONID, iconId)
            }

            AlertDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(activity.supportFragmentManager, tag)
        }
    }

    /**
     * Marks the `AlertDialogFragment` DSL's objects.
     *
     * @since 1.0.0
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Shows an alert dialog.
 *
 * @receiver      The activity.
 * @param    init The initialization block.
 * @since         1.0.0
 */
public fun FragmentActivity.showAlertDialog(
        init: AlertDialogFragment.Builder.() -> Unit
) = AlertDialogFragment.Builder(this).apply(init).show()
