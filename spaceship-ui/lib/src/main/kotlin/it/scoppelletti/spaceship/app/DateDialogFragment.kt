/*
 * Copyright (C) 2013-2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import it.scoppelletti.spaceship.i18n.I18NProvider
import org.threeten.bp.LocalDate

/**
 * Date picker dialog.
 *
 * * [Creating a Date Picker](http://developer.android.com/guide/topics/ui/controls/pickers#DatePicker)
 *
 * @since 1.0.0
 */
public class DateDialogFragment : DialogFragment() {

    private lateinit var pickerDlg: DatePickerDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        pickerDlg = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener
                { _, year, month, dayOfMonth ->
                    onDateSet(year, month, dayOfMonth)
                }, 1970, 0, 1)

        return pickerDlg
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val args: Bundle
        val i18nProvider: I18NProvider
        val epochDay: Long
        val date: LocalDate

        super.onActivityCreated(savedInstanceState)

        args = arguments!!
        epochDay = args.getLong(DateDialogFragment.PROP_EPOCHDAY,
                Long.MIN_VALUE)
        date = if (epochDay == Long.MIN_VALUE) {
            i18nProvider = requireActivity().stdlibComponent().i18nProvider()
            LocalDate.now(i18nProvider.currentZoneId())
        } else {
            LocalDate.ofEpochDay(epochDay)
        }

        pickerDlg.updateDate(date.year, date.monthValue - 1, date.dayOfMonth)
    }

    /**
     * Handles the date set by the user.
     *
     * @param year  Year.
     * @param month Month.
     * @param day   Day.
     */
    private fun onDateSet(year: Int, month: Int, day: Int) {
        tag?.let { dialogTag ->
            val parent: DateDialogFragment.OnDateSetListener?

            parent = (parentFragment ?: activity) as?
                    DateDialogFragment.OnDateSetListener
            parent?.onDateSet(dialogTag, LocalDate.of(year, month + 1, day))
        }
    }

    public companion object {

        /**
         * Fragment tag.
         */
        public const val TAG: String = AppExt.TAG_DATEDIALOG

        private const val PROP_EPOCHDAY = "1"
    }

    /**
     * Handles the date set by the user.
     *
     * @since 1.0.0
     */
    public interface OnDateSetListener {

        /**
         * This method will be invoked when the user is done filling in the
         * date.
         *
         * @param tag   Fragment tag.
         * @param value Date set by the user.
         */
        fun onDateSet(tag: String, value: LocalDate)
    }

    /**
     * Builds a `DateDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    @DateDialogFragment.Dsl
    public class Builder(
            private val fragmentManager: FragmentManager
    ) {
        private var _tag: String = DateDialogFragment.TAG
        private var _initialValue: LocalDate? = null

        /**
         * Defines the fragment tag.
         *
         * @param init Initialization block.
         */
        public fun tag(init: () -> String) {
            _tag = init()
        }

        /**
         * Defines the initial value.
         *
         * @param init Initialization block.
         */
        public fun initialValue(init: () -> LocalDate?) {
            _initialValue = init()
        }

        internal fun show() {
            val args: Bundle

            args = Bundle()
            _initialValue?.let {
                args.putLong(DateDialogFragment.PROP_EPOCHDAY, it.toEpochDay())
            }

            DateDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(fragmentManager, _tag)
        }
    }

    /**
     * Marks the `DateDialogFragment` DSL's objects.
     *
     * @since 1.0.0
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Shows a Date Picker dialog.
 *
 * @receiver      Activity.
 * @param    init Initialization block.
 * @since         1.0.0
 */
@UiThread
public fun FragmentActivity.showDateDialog(
        init: DateDialogFragment.Builder.() -> Unit = { }
) = DateDialogFragment.Builder(this.supportFragmentManager).apply(init).show()

/**
 * Shows a Date Picker dialog.
 *
 * @receiver      Fragment.
 * @param    init Initialization block.
 * @since         1.0.0
 */
@UiThread
public fun Fragment.showDateDialog(
        init: DateDialogFragment.Builder.() -> Unit = { }
) = DateDialogFragment.Builder(this.childFragmentManager).apply(init).show()
