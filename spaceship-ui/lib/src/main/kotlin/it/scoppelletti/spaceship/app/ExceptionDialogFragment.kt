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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.app

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.ExceptionLogger
import it.scoppelletti.spaceship.content.res.ResourcesExt
import it.scoppelletti.spaceship.lifecycle.ExceptionActivityState
import it.scoppelletti.spaceship.lifecycle.ExceptionDialogState
import it.scoppelletti.spaceship.lifecycle.ExceptionDialogModel
import it.scoppelletti.spaceship.lifecycle.ExceptionActivityModel
import it.scoppelletti.spaceship.widget.ExceptionListAdapter

/**
 * Exception dialog.
 *
 * @since 1.0.0
 */
@UiThread
public class ExceptionDialogFragment : AppCompatDialogFragment() {

    private lateinit var viewModel: ExceptionDialogModel
    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var adapter: ExceptionListAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle
        val titleId: Int

        args = arguments!!
        titleId = args.getInt(ExceptionDialogFragment.PROP_TITLEID,
                android.R.string.dialog_alert_title)
        adapter = ExceptionListAdapter(requireContext())

        return AlertDialog.Builder(requireContext())
                .setTitle(titleId)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setAdapter(adapter, null)
                .setNegativeButton(android.R.string.cancel, ::onDialogResult)
                .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val activity: Activity
        val exState: ExceptionActivityState?
        val activityModel: ExceptionActivityModel

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()
        activityModel = ViewModelProviders.of(activity)
                .get(ExceptionActivityModel::class.java)

        viewModelFactory = activity.uiComponent().viewModelFactory()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ExceptionDialogModel::class.java)

        viewModel.state.observe(this, Observer<ExceptionDialogState> { state ->
            if (state != null) {
                adapter.addAll(state.exList)
            }
        })

        exState = activityModel.state
        if (exState != null) {
            activityModel.state = null
            viewModel.load(exState)
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
        public const val TAG: String = AppExt.TAG_EXCEPTIONDIALOG

        private const val PROP_TITLEID = "1"
    }

    /**
     * Builds an `ExceptionDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    @ExceptionDialogFragment.Dsl
    public class Builder internal constructor(
            private val activity: FragmentActivity,
            private val ex: Throwable
    ) {
        private var _tag: String = ExceptionDialogFragment.TAG
        private var titleId: Int = ResourcesExt.ID_NULL

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
         * Defines the title.
         *
         * @param init Initialization block.
         */
        public fun title(init: () -> Int) {
            titleId = init()
        }

        internal fun show() {
            val args: Bundle
            val viewModel: ExceptionActivityModel
            val exLogger: ExceptionLogger

            exLogger = activity.stdlibComponent().exceptionLogger()
            exLogger.log(ex)

            args = Bundle()
            if (titleId != ResourcesExt.ID_NULL) {
                args.putInt(ExceptionDialogFragment.PROP_TITLEID, titleId)
            }

            viewModel = ViewModelProviders.of(activity)
                    .get(ExceptionActivityModel::class.java)
            viewModel.state = ExceptionActivityState(ex)

            ExceptionDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(activity.supportFragmentManager, _tag)
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
@UiThread
public fun FragmentActivity.showExceptionDialog(
        ex: Throwable,
        init: ExceptionDialogFragment.Builder.() -> Unit = { }
) = ExceptionDialogFragment.Builder(this, ex).apply(init).show()
