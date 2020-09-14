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
        "RemoveRedundantQualifierName", "unused")

package it.scoppelletti.spaceship.app

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.scoppelletti.spaceship.content.res.ResourcesExt
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.lifecycle.AlertActivityModel
import it.scoppelletti.spaceship.lifecycle.AlertDialogModel
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import it.scoppelletti.spaceship.types.StringExt

/**
 * Alert dialog.
 *
 * @see   it.scoppelletti.spaceship.app.OnDialogResultListener
 * @since 1.0.0
 */
@UiThread
public class AlertDialogFragment : AppCompatDialogFragment() {

    private lateinit var viewModel: AlertDialogModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle
        val title: String?
        val builder: MaterialAlertDialogBuilder
        var resId: Int

        args = requireArguments()
        builder = MaterialAlertDialogBuilder(requireActivity())
                .setMessage(StringExt.EMPTY)

        title = args.getString(AlertDialogFragment.PROP_TITLE)
        if (title.isNullOrBlank()) {
            resId = args.getInt(AlertDialogFragment.PROP_TITLEID,
                    android.R.string.dialog_alert_title)
            builder.setTitle(resId)
        } else {
            builder.setTitle(title)
        }

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
        val activity: FragmentActivity
        val activityModel: AlertActivityModel
        val viewModelProvider: ViewModelProviderEx

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()
        viewModelProvider = activity.appComponent().viewModelProvider()
        activityModel = ViewModelProvider(activity)
                .get(AlertActivityModel::class.java)
        viewModel = viewModelProvider.get(this, AlertDialogModel::class.java)

        @Suppress("FragmentLiveDataObserve")
        viewModel.message.observe(this) { message ->
            if (message != null) {
                (dialog as AlertDialog).setMessage(message)
            }
        }

        activityModel.message?.let {
            activityModel.message = null
            viewModel.load(it)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
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
        tag?.let { dialogTag ->
            val parent: OnDialogResultListener?

            parent = (parentFragment ?: activity) as? OnDialogResultListener
            parent?.onDialogResult(dialogTag, which)
        }
    }

    public companion object {

        /**
         * Fragment tag.
         */
        public const val TAG: String = AppExt.TAG_ALERTDIALOG

        private const val PROP_TITLE = "1"
        private const val PROP_TITLEID = "2"
        private const val PROP_POSITIVEID = "3"
        private const val PROP_NEGATIVEID = "4"
        private const val PROP_NEUTRALID = "5"
        private const val PROP_ICONID = "6"
    }

    /**
     * Builds an `AlertDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    @AlertDialogFragment.Dsl
    public class Builder(
            private val activity: AppCompatActivity,
            private val fragmentMgr: FragmentManager
    ) {
        private var _tag: String = AlertDialogFragment.TAG
        private var _msg: MessageSpec? = null

        @StringRes
        private var _titleId = ResourcesExt.ID_NULL

        private var _title: String? = null

        @StringRes
        private var _positiveActionTextId: Int = ResourcesExt.ID_NULL

        @StringRes
        private var _negativeActionTextId: Int = ResourcesExt.ID_NULL

        @StringRes
        private var _neutralActionTextId: Int = ResourcesExt.ID_NULL

        @DrawableRes
        private var _iconId: Int = ResourcesExt.ID_NULL

        /**
         * Defines the fragment tag.
         *
         * @param init Initialization block.
         */
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
         * Defines the title as a string resource ID..
         *
         * @param init Initialization block.
         */
        public fun titleId(init: () -> Int) {
            _titleId = init()
        }

        /**
         * Defines the title.
         *
         * @param init Initialization block.
         */
        public fun title(init: () -> String) {
            _title = init()
        }

        /**
         * Defines the positive action text as a string resource ID.
         *
         * @param init Initialization block.
         */
        public fun positiveActionTextId(init: () -> Int) {
            _positiveActionTextId = init()
        }

        /**
         * Defines the negative action text as a string resource ID.
         *
         * @param init Initialization block.
         */
        public fun negativeActionTextId(init: () -> Int) {
            _negativeActionTextId = init()
        }

        /**
         * Defines the neutral action text as a string resource ID.
         *
         * @param init Initialization block.
         */
        public fun neutralActionTextId(init: () -> Int) {
            _neutralActionTextId = init()
        }

        /**
         * Defines the icon as a `Drawable` resource ID.
         *
         * @param init Initialization block.
         */
        public fun iconId(init: () -> Int) {
            _iconId = init()
        }

        internal fun show() {
            val args: Bundle
            val viewModel: AlertActivityModel
            val msg: MessageSpec

            msg = _msg ?: throw NullPointerException(
                    "Missing the MessageSpec object.")

            args = Bundle()

            if (!_title.isNullOrBlank()) {
                args.putString(AlertDialogFragment.PROP_TITLE, _title)
            } else if (_titleId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_TITLEID, _titleId)
            }

            if (_positiveActionTextId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_POSITIVEID,
                        _positiveActionTextId)
            }
            if (_negativeActionTextId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_NEGATIVEID,
                        _negativeActionTextId)
            }
            if (_neutralActionTextId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_NEUTRALID,
                        _neutralActionTextId)
            }
            if (_iconId != ResourcesExt.ID_NULL) {
                args.putInt(AlertDialogFragment.PROP_ICONID, _iconId)
            }

            viewModel = ViewModelProvider(activity)
                    .get(AlertActivityModel::class.java)
            viewModel.message = msg

            AlertDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(fragmentMgr, _tag)
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
@UiThread
public fun AppCompatActivity.showAlertDialog(
        init: AlertDialogFragment.Builder.() -> Unit = { }
) = AlertDialogFragment.Builder(this, this.supportFragmentManager)
        .apply(init)
        .show()

/**
 * Shows an alert dialog.
 *
 * @receiver      Fragment.
 * @param    init Initialization block.
 * @since         1.0.0
 */
@UiThread
public fun Fragment.showAlertDialog(
        init: AlertDialogFragment.Builder.() -> Unit = { }
) = AlertDialogFragment.Builder(this.requireActivity() as AppCompatActivity,
        this.childFragmentManager)
        .apply(init)
        .show()

