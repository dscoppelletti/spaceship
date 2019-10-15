/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import it.scoppelletti.spaceship.i18n.I18NProvider
import org.threeten.bp.LocalTime

/**
 * Time picker dialog.
 *
 * * [Creating a Time Picker](http://developer.android.com/guide/topics/ui/controls/pickers#TimePicker)
 *
 * @since 1.0.0
 */
public class TimeDialogFragment : DialogFragment() {

    private lateinit var pickerDlg: TimePickerDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = requireContext()

        pickerDlg = TimePickerDialog(ctx,
                TimePickerDialog.OnTimeSetListener
                { _, hour, minute ->
                    onTimeSet(hour, minute)
                }, 0, 0, DateFormat.is24HourFormat(ctx))

        return pickerDlg
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val args: Bundle
        val i18nProvider: I18NProvider
        val secDay: Int
        val time: LocalTime

        super.onActivityCreated(savedInstanceState)

        args = arguments!!
        secDay = args.getInt(TimeDialogFragment.PROP_SECDAY, -1)
        time = if (secDay < 0) {
            i18nProvider = requireActivity().stdlibComponent().i18nProvider()
            LocalTime.now(i18nProvider.currentZoneId())
        } else {
            LocalTime.ofSecondOfDay(secDay.toLong())
        }

        pickerDlg.updateTime(time.hour, time.minute)
    }

    /**
     * Handle the time set by the user.
     *
     * @param hour   Hour.
     * @param minute Minute.
     */
    private fun onTimeSet(hour: Int, minute: Int) {
        val dialogTag: String?
        val activity: FragmentActivity

        dialogTag = tag
        activity = requireActivity()

        if (dialogTag != null &&
                activity is TimeDialogFragment.OnTimeSetListener) {
            activity.onTimeSet(dialogTag, LocalTime.of(hour, minute))
        }
    }

    public companion object {

        /**
         * Fragment tag.
         */
        public const val TAG: String = AppExt.TAG_TIMEDIALOG

        private const val PROP_SECDAY = "1"
    }

    /**
     * Handles the date set by the user.
     *
     * @since 1.0.0
     */
    public interface OnTimeSetListener {

        /**
         * This method will be invoked when the user is done filling in the
         * time.
         *
         * @param tag   Fragment tag.
         * @param value Time set by the user.
         */
        fun onTimeSet(tag: String, value: LocalTime)
    }

    /**
     * Builds a `TimeDialogFragment` fragment.
     *
     * @since 1.0.0
     */
    @TimeDialogFragment.Dsl
    public class Builder(
            private val activity: FragmentActivity
    ) {
        private var _tag: String = TimeDialogFragment.TAG
        private var _initialValue: LocalTime? = null

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
        public fun initialValue(init: () -> LocalTime?) {
            _initialValue = init()
        }

        internal fun show() {
            val args: Bundle

            args = Bundle()
            _initialValue?.let {
                args.putInt(TimeDialogFragment.PROP_SECDAY, it.toSecondOfDay())
            }

            TimeDialogFragment()
                    .apply {
                        arguments = args
                    }
                    .show(activity.supportFragmentManager, _tag)
        }
    }

    /**
     * Marks the `TimeDialogFragment` DSL's objects.
     *
     * @since 1.0.0
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Shows a Time Picker dialog.
 *
 * @receiver      Activity.
 * @param    init Initialization block.
 * @since         1.0.0
 */
@UiThread
public fun FragmentActivity.showTimeDialog(
        init: TimeDialogFragment.Builder.() -> Unit = { }
) = TimeDialogFragment.Builder(this).apply(init).show()
