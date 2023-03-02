/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.widget

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.UiThread
import it.scoppelletti.spaceship.os.parcelableCreator
import it.scoppelletti.spaceship.os.ParcelableExt

private const val ALPHA_GONE = 0
private const val ALPHA_VISIBLE = 102
private const val SHOW_DELAY: Long = 500

/**
 * Circular indeterminate progress indicator within a screen overlay.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       ctx          Context.
 * @param       attrs        Attributes.
 * @param       defStyleAttr Default style as theme attribure ID.
 */
@UiThread
public class ProgressOverlay @JvmOverloads constructor(
        ctx: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int = 0
): FrameLayout(ctx, attrs, defStyleAttr) {

    private val indicator: ProgressBar

    init {
        visibility = View.GONE
        isClickable = true

        val color = Color.argb(ALPHA_GONE, 0, 0, 0)
        setBackgroundColor(color)

        indicator = ProgressBar(context).apply {
            isIndeterminate = true
            visibility = View.GONE
        }

        val layout = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        addView(indicator, layout)
    }

    /**
     * Indicates whether this progress indicator is running.
     */
    @Suppress("WeakerAccess")
    public val isRunning: Boolean
        get() = (visibility == View.VISIBLE)

    /**
     * Shows this progress indicator.
     */
    public fun show() {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
            postDelayed(::delayedShow, SHOW_DELAY)
        }
    }

    /**
     * Shows this progress indicator.
     */
    private fun delayedShow() {
        if (visibility == View.VISIBLE) {
            doShow()
        }
    }

    /**
     * Shows this progress indicator.
     */
    private fun doShow() {
        visibility = View.VISIBLE
        val color = Color.argb(ALPHA_VISIBLE, 0, 0, 0)
        setBackgroundColor(color)
        indicator.visibility = View.VISIBLE
    }

    /**
     * Hides this progress indicator.
     */
    public fun hide() {
        if (visibility != View.GONE) {
            doHide()
        }
    }

    /**
     * Hides this progress indicator.
     */
    private fun doHide() {
        indicator.visibility = View.GONE
        val color = Color.argb(ALPHA_GONE, 0, 0, 0)
        setBackgroundColor(color)
        visibility = View.GONE
    }

    override fun dispatchSaveInstanceState(
            container: SparseArray<Parcelable>?
    ) {
        // Save the state for the container only; disable the saving for the
        // contained controls.
        super.dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(
            container: SparseArray<Parcelable>?
    ) {
        // Restore the state for the container only; disable the restoring for
        // the contained controls.
        super.dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState() ?: return null

        return SavedState(superState)
                .apply {
                    running = isRunning
                }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)

            if (state.running) {
                doShow()
            } else {
                doHide()
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Component state.
     */
    public class SavedState : BaseSavedState {

        /**
         * Indicates whether the progress indicator is running or not.
         */
        public var running: Boolean = false

        /**
         * @constructor        Constructor.
         * @param       source Input stream.
         */
        public constructor(source: Parcelable) : super(source)

        /**
         * @constructor        Constructor.
         * @param       source Input stream.
         */
        private constructor(source: Parcel) : super(source) {
            running = ParcelableExt.readBoolean(source)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            ParcelableExt.writeBoolean(out, running)
        }

        public companion object {

            /**
             * The `Parcelable` support.
             */
            @JvmField
            public val CREATOR: Parcelable.Creator<SavedState> =
                parcelableCreator(::SavedState)
        }
    }
}
