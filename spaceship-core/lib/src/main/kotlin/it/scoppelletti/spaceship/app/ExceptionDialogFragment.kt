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
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.MessageBuilder
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.lifecycle.ExceptionListState
import it.scoppelletti.spaceship.lifecycle.ExceptionListViewModel
import it.scoppelletti.spaceship.lifecycle.ExceptionViewModel
import it.scoppelletti.spaceship.widget.ExceptionListAdapter
import javax.inject.Inject

/**
 * Exception dialog.
 *
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
@UiThread
public class ExceptionDialogFragment : AppCompatDialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ExceptionListViewModel
    private lateinit var adapter: ExceptionListAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val resId: Int
        val msg: String?
        val args: Bundle
        val builder: AlertDialog.Builder

        adapter = ExceptionListAdapter(requireContext())

        args = arguments!!
        builder = AlertDialog.Builder(requireContext())
                .setAdapter(adapter, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, ::onDialogResult)

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val ex: Throwable
        val activityModel: ExceptionViewModel

        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ExceptionListViewModel::class.java)

        if (viewModel.outerEx == null) {
            activityModel = ViewModelProviders.of(requireActivity())
                    .get(ExceptionViewModel::class.java)
            ex = activityModel.ex ?:
                    throw IllegalStateException("Exception is not set.")
            activityModel.ex = null
            viewModel.outerEx = ex
        }

        viewModel.state.observe(this, Observer<ExceptionListState> { state ->
            if (state != null) {
                adapter.addAll(state.exList)
            }
        })

        viewModel.load()
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

        private const val PROP_TITLE: String = "1"
        private const val PROP_TITLEID: String = "2"
    }

    /**
     * Builds an `ExceptionDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    public interface Builder {

        /**
         * Shows an exception dialog.
         *
         * @param  activity Activity.
         * @param  ex       Exception.
         * @param  init     Initialization block.
         */
        fun show(
                activity: FragmentActivity,
                ex: Throwable,
                init: ExceptionDialogFragment.BuilderDsl.() -> Unit = { }
        )
    }

    /**
     * Builds an `ExceptionDialogFragment` fragment.
     *
     * @since 1.0.0
     *
     * @property tag Fragment tag.
     */
    @MessageBuilder.Dsl
    @ExceptionDialogFragment.Dsl
    public class BuilderDsl internal constructor(
            private val activity: FragmentActivity,
            private val ex: Throwable,
            private val viewModelFactory : ViewModelProvider.Factory
    ) {
        public var tag: String = ExceptionDialogFragment.TAG
        private var titleBuilder: MessageBuilder? = null

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

        /**
         * Shows the dialog.
         */
        internal fun show() {
            val args: Bundle
            val viewModel: ExceptionViewModel

            viewModel = ViewModelProviders.of(activity, viewModelFactory).get(
                    ExceptionViewModel::class.java)
            viewModel.ex = ex
            viewModel.log()

            args = Bundle()

            titleBuilder?.let {
                if (it.isSimple) {
                    args.putInt(ExceptionDialogFragment.PROP_TITLEID,
                            it.messageId)
                } else {
                    args.putString(ExceptionDialogFragment.PROP_TITLE,
                            it.build(activity.resources))
                }
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
 * Implementation of the `ExceptionDialogBuilder` interface.
 *
 * @since 1.0.0
 *
 * @constructor                  Constructor.
 * @param       viewModelFactory Implementation of the
 *                               `ViewModelProvider.Factory` interface.
 */
public class ExceptionDialogBuilder @Inject constructor(
        private val viewModelFactory : ViewModelProvider.Factory
) : ExceptionDialogFragment.Builder {

    override fun show(
            activity: FragmentActivity,
            ex: Throwable,
            init: ExceptionDialogFragment.BuilderDsl.() -> Unit
    ) = ExceptionDialogFragment.BuilderDsl(activity, ex, viewModelFactory)
            .apply(init)
            .show()
}
