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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.app

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.content.res.ResourcesExt
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.lifecycle.AlertActivityModel
import it.scoppelletti.spaceship.lifecycle.AlertActivityState
import it.scoppelletti.spaceship.lifecycle.AlertDialogModel
import it.scoppelletti.spaceship.lifecycle.AlertDialogState
import it.scoppelletti.spaceship.types.StringExt

/**
 * Alert dialog.
 *
 * @since 1.0.0
 */
@UiThread
public class AlertDialogFragment : AppCompatDialogFragment() {

    private lateinit var viewModel: AlertDialogModel
    private lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle
        val builder: AlertDialog.Builder
        var resId: Int

        args = arguments!!
        builder = AlertDialog.Builder(requireActivity())
                .setMessage(StringExt.EMPTY)

        resId = args.getInt(AlertDialogFragment.PROP_TITLEID,
                android.R.string.dialog_alert_title)
        builder.setTitle(resId)

        resId = args.getInt(AlertDialogFragment.PROP_POSITIVEID,
                android.R.string.ok)
        builder.setPositiveButton(resId, ::onDialogResult)

        resId = args.getInt(AlertDialogFragment.PROP_NEGATIVEID,
                android.R.string.cancel)
        builder.setNegativeButton(resId, ::onDialogResult)

        resId = args.getInt(AlertDialogFragment.PROP_NEUTRALID,
                ResourcesExt.ID_NULL)
        if (resId != ResourcesExt.ID_NULL) {
            builder.setNeutralButton(resId, ::onDialogResult)
        }

        resId = args.getInt(AlertDialogFragment.PROP_ICONID,
                ResourcesExt.ID_NULL)
        if (resId != ResourcesExt.ID_NULL) {
            builder.setIcon(resId)
        }

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val activity: Activity
        val alertState: AlertActivityState?
        val activityModel: AlertActivityModel

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()
        activityModel = ViewModelProviders.of(activity)
                .get(AlertActivityModel::class.java)

        viewModelFactory = activity.uiComponent().viewModelFactory()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AlertDialogModel::class.java)

        viewModel.state.observe(this, Observer<AlertDialogState> { state ->
            if (state != null) {
                (dialog as AlertDialog).setMessage(state.message)
            }
        })

        alertState = activityModel.state
        if (alertState != null) {
            activityModel.state = null
            viewModel.load(alertState)
        }
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
        public const val TAG: String = AppExt.TAG_ALERTDIALOG

        private const val PROP_TITLEID = "1"
        private const val PROP_POSITIVEID = "2"
        private const val PROP_NEGATIVEID = "3"
        private const val PROP_NEUTRALID = "4"
        private const val PROP_ICONID = "5"
    }

    /**
     * Builds an `AlertDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    @AlertDialogFragment.Dsl
    public class Builder(
            private val activity: FragmentActivity
    ) {
        private var _tag: String = AlertDialogFragment.TAG
        private var _msg: MessageSpec? = null

        @StringRes
        private var titleId = ResourcesExt.ID_NULL

        @StringRes
        private var positiveActionTextId: Int = ResourcesExt.ID_NULL

        @StringRes
        private var negativeActionTextId: Int = ResourcesExt.ID_NULL

        @StringRes
        private var neutralActionTextId: Int = ResourcesExt.ID_NULL

        @DrawableRes
        private var iconId: Int = ResourcesExt.ID_NULL

        /**
         * Defines the fragment tag.
         *
         * @param init Initialization block.
         */
        @Suppress("unused")
        public fun tag(init: () -> String) {
            _tag = init()
        }

        /**
         * Defines the message.
         *
         * @param init Initialization block.
         */
        public fun message(init: () -> MessageSpec) {
            _msg = init()
        }

        /**
         * Defines the title.
         *
         * @param init Initialization block.
         */
        public fun title(init: () -> Int) {
            titleId = init()
        }

        /**
         * Defines the positive action text as a string resource ID.
         *
         * @param init Initialization block.
         */
        @Suppress("unused")
        public fun positiveActionText(init: () -> Int) {
            positiveActionTextId = init()
        }

        /**
         * Defines the negative action text as a string resource ID.
         *
         * @param init Initialization block.
         */
        @Suppress("unused")
        public fun negativeActionText(init: () -> Int) {
            negativeActionTextId = init()
        }

        /**
         * Defines the neutral action text as a string resource ID.
         *
         * @param init Initialization block.
         */
        @Suppress("unused")
        public fun neutralActionText(init: () -> Int) {
            neutralActionTextId = init()
        }

        /**
         * Defines the icon as a `Drawable` resource ID..
         *
         * @param init Initialization block.
         */
        @Suppress("unused")
        public fun icon(init: () -> Int) {
            iconId = init()
        }

        internal fun show() {
            val args: Bundle
            val msg: MessageSpec
            val viewModel: AlertActivityModel

            msg = _msg ?: throw NullPointerException(
                    "Missing the MessageSpec object.")

            args = Bundle()
            if (titleId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_TITLEID, titleId)
            }
            if (positiveActionTextId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_POSITIVEID,
                        positiveActionTextId)
            }
            if (negativeActionTextId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_NEGATIVEID,
                        negativeActionTextId)
            }
            if (neutralActionTextId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_NEUTRALID,
                        neutralActionTextId)
            }
            if (iconId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_ICONID, iconId)
            }

            viewModel = ViewModelProviders.of(activity)
                    .get(AlertActivityModel::class.java)
            viewModel.state = AlertActivityState(msg)

            AlertDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(activity.supportFragmentManager, _tag)
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
 * Shows an exception dialog.
 *
 * @receiver      Activity.
 * @param    init Initialization block.
 * @since         1.0.0
 */
@UiThread
public fun FragmentActivity.showAlertDialog(
        init: AlertDialogFragment.Builder.() -> Unit = { }
) = AlertDialogFragment.Builder(this).apply(init).show()
