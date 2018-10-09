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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
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
        val args: Bundle
        val builder: AlertDialog.Builder
        var msg: String?
        var resId: Int

        args = arguments!!
        builder = AlertDialog.Builder(requireActivity())

        msg = args.getString(AlertDialogFragment.PROP_MSG)
        if (msg.isNullOrBlank()) {
            builder.setMessage(args.getInt(AlertDialogFragment.PROP_MSGID))
        } else {
            builder.setMessage(msg)
        }

        msg = args.getString(AlertDialogFragment.PROP_TITLE)
        if (msg.isNullOrBlank()) {
            resId = args.getInt(AlertDialogFragment.PROP_TITLEID,
                    android.R.string.dialog_alert_title)
            builder.setTitle(resId)
        } else {
            builder.setTitle(msg)
        }

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
     * @param dialog Dialog that received the click.
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
         * Fragment tag.
         */
        public const val TAG: String = CoreExt.TAG_ALERTDIALOG

        private const val PROP_MSG: String = "1"
        private const val PROP_MSGID: String = "2"
        private const val PROP_TITLE: String = "3"
        private const val PROP_TITLEID: String = "4"
        private const val PROP_POSITIVEID: String = "5"
        private const val PROP_NEGATIVEID: String = "6"
        private const val PROP_NEUTRALID: String = "7"
        private const val PROP_ICONID: String = "8"
    }

    /**
     * Builds an `AlertDialogFragment` fragment.
     *
     * @since 1.0.0
     *
     * @property positiveActionTextId Positive action text as a string resource
     *                                ID.
     * @property negativeActionTextId Negative action text as a string resource
     *                                ID.
     * @property neutralActionTextId  Neutral action text as a string resource
     *                                ID.
     * @property iconId               Icon as a `Drawable` resource ID.
     * @property tag                  Fragment tag.
     */
    @MessageBuilder.Dsl
    @AlertDialogFragment.Dsl
    public class Builder internal constructor(
            private val activity: FragmentActivity
    ) {

        @StringRes
        public var positiveActionTextId: Int = android.R.string.ok

        @StringRes
        public var negativeActionTextId: Int = android.R.string.cancel

        @StringRes
        public var neutralActionTextId: Int = 0

        @DrawableRes
        public var iconId: Int = 0

        public var tag: String = AlertDialogFragment.TAG
        private var messageBuilder: MessageBuilder? = null
        private var titleBuilder: MessageBuilder? = null

        /**
         * Defines the message.
         *
         * @param  messageId Message as a string resource ID.
         * @param  init      Initializiation block.
         * @return           The new object.
         */
        public fun message(
                @StringRes messageId: Int,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            messageBuilder = MessageBuilder.make(messageId, init)
            return messageBuilder!!
        }

        /**
         * Defines the message.
         *
         * @param  message Message.
         * @param  init    Initializiation block.
         * @return         The new object.
         */
        public fun message(
                message: String,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            messageBuilder = MessageBuilder.make(message, init)
            return messageBuilder!!
        }

        /**
         * Defines the title.
         *
         * @param  titleId Title as a string resource ID.
         * @param  init    Initialization block.
         * @return         The new object.
         */
        public fun title(
                @StringRes titleId: Int,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            titleBuilder = MessageBuilder.make(titleId, init)
            return titleBuilder!!
        }

        /**
         * Defines the title.
         *
         * @param  title Title.
         * @param  init  Initialization block.
         * @return       The new object.
         */
        public fun title(
                title: String,
                init: MessageBuilder.() -> Unit = { }
        ): MessageBuilder {
            titleBuilder = MessageBuilder.make(title, init)
            return titleBuilder!!
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

            titleBuilder?.let { title ->
                if (title.isSimple) {
                    args.putInt(AlertDialogFragment.PROP_TITLEID,
                            title.messageId)
                } else {
                    args.putString(AlertDialogFragment.PROP_TITLE,
                            title.build(activity.resources))
                }
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
 * @receiver      Activity.
 * @param    init Initialization block.
 * @since         1.0.0
 */
public fun FragmentActivity.showAlertDialog(
        init: AlertDialogFragment.Builder.() -> Unit
) = AlertDialogFragment.Builder(this).apply(init).show()
