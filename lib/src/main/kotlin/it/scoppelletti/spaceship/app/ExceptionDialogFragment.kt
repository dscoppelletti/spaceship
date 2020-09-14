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
        "RemoveRedundantQualifierName", "unused")

package it.scoppelletti.spaceship.app

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.scoppelletti.spaceship.ExceptionLogger
import it.scoppelletti.spaceship.content.res.ResourcesExt
import it.scoppelletti.spaceship.lifecycle.ExceptionActivityModel
import it.scoppelletti.spaceship.lifecycle.ExceptionDialogModel
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import it.scoppelletti.spaceship.widget.ExceptionListAdapter

/**
 * Exception dialog.
 *
 * @see   it.scoppelletti.spaceship.app.OnDialogResultListener
 * @since 1.0.0
 */
@UiThread
public class ExceptionDialogFragment : AppCompatDialogFragment() {

    private lateinit var viewModel: ExceptionDialogModel
    private lateinit var adapter: ExceptionListAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle
        val titleId: Int
        val title: String?
        val builder: MaterialAlertDialogBuilder

        args = requireArguments()
        title = args.getString(ExceptionDialogFragment.PROP_TITLE)
        adapter = ExceptionListAdapter(requireContext())

        builder = MaterialAlertDialogBuilder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setAdapter(adapter, null)
                .setNegativeButton(android.R.string.cancel, ::onDialogResult)

        if (title.isNullOrBlank()) {
            titleId = args.getInt(ExceptionDialogFragment.PROP_TITLEID,
                    android.R.string.dialog_alert_title)
            builder.setTitle(titleId)
        } else {
            builder.setTitle(title)
        }

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val activity: FragmentActivity
        val activityModel: ExceptionActivityModel
        val viewModelProvider: ViewModelProviderEx

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()
        viewModelProvider = activity.appComponent().viewModelProvider()
        activityModel = ViewModelProvider(activity)
                .get(ExceptionActivityModel::class.java)
        viewModel = viewModelProvider.get(this,
                ExceptionDialogModel::class.java)

        @Suppress("FragmentLiveDataObserve")
        viewModel.state.observe(this) { state ->
            if (state != null) {
                adapter.addAll(state.exList)
            }
        }

        activityModel.ex?.let {
            activityModel.ex = null
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
     *               (`DialogInterface.BUTTON_NEGATIVE`).
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
        public const val TAG: String = AppExt.TAG_EXCEPTIONDIALOG

        private const val PROP_TITLE = "1"
        private const val PROP_TITLEID = "2"
    }

    /**
     * Builds an `ExceptionDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    @ExceptionDialogFragment.Dsl
    public class Builder internal constructor(
            private val activity: AppCompatActivity,
            private val fragmentMgr: FragmentManager,
            private val ex: Throwable
    ) {
        private var _tag: String = ExceptionDialogFragment.TAG

        @StringRes
        private var _titleId: Int = ResourcesExt.ID_NULL

        private var _title: String? = null

        /**
         * Defines the fragment tag.
         *
         * @param init Initialization block.
         */
        public fun tag(init: () -> String) {
            _tag = init()
        }

        /**
         * Defines the title as a string resource ID.
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

        internal fun show() {
            val args: Bundle
            val viewModel: ExceptionActivityModel
            val exLogger: ExceptionLogger

            exLogger = activity.stdlibComponent().exceptionLogger()
            exLogger.log(ex)

            args = Bundle()
            if (!_title.isNullOrBlank()) {
                args.putString(ExceptionDialogFragment.PROP_TITLE, _title)
            } else if (_titleId != ResourcesExt.ID_NULL) {
                args.putInt(ExceptionDialogFragment.PROP_TITLEID, _titleId)
            }

            viewModel = ViewModelProvider(activity)
                    .get(ExceptionActivityModel::class.java)
            viewModel.ex = ex

            ExceptionDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(fragmentMgr, _tag)
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
public fun AppCompatActivity.showExceptionDialog(
        ex: Throwable,
        init: ExceptionDialogFragment.Builder.() -> Unit = { }
) = ExceptionDialogFragment.Builder(this, this.supportFragmentManager, ex)
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
public fun Fragment.showExceptionDialog(
        ex: Throwable,
        init: ExceptionDialogFragment.Builder.() -> Unit = { }
) = ExceptionDialogFragment.Builder(this.requireActivity() as AppCompatActivity,
        this.childFragmentManager, ex)
        .apply(init)
        .show()
