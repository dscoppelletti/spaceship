/*
 * Copyright (C) 2013-2021 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import it.scoppelletti.spaceship.i18n.I18NProvider
import java.time.LocalDate

/**
 * Date picker dialog.
 *
 * * [Creating a Date Picker](http://developer.android.com/guide/topics/ui/controls/pickers#DatePicker)
 *
 * @since 1.0.0
 */
public class DateDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle
        val i18nProvider: I18NProvider
        val epochDay: Long
        val date: LocalDate
        val pickerDlg: DatePickerDialog

        args = requireArguments()
        epochDay = args.getLong(DateDialogFragment.PROP_EPOCHDAY,
                Long.MIN_VALUE)
        date = if (epochDay == Long.MIN_VALUE) {
            i18nProvider = requireActivity().stdlibComponent().i18nProvider()
            LocalDate.now(i18nProvider.currentZoneId())
        } else {
            LocalDate.ofEpochDay(epochDay)
        }

        pickerDlg = DatePickerDialog(requireContext(),
                { _, year, month, dayOfMonth ->
                    onDateSet(year, month, dayOfMonth)
                }, date.year, date.monthValue - 1, date.dayOfMonth)

        return pickerDlg
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
            setFragmentResult(dialogTag, bundleOf(
                    DateDialogFragment.PROP_RESULT to
                            LocalDate.of(year, month + 1, day)))
        }
    }

    public companion object {

        /**
         * Fragment tag.
         */
        public const val TAG: String = AppExt.TAG_DATEDIALOG

        /**
         * Property containing the `LocalDate` object set by the user.
         */
        public const val PROP_RESULT = AppExt.PROP_RESULT

        private const val PROP_EPOCHDAY = "1"
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
