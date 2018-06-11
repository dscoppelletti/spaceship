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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import it.scoppelletti.spaceship.R
import it.scoppelletti.spaceship.os.parcelableCreator
import it.scoppelletti.spaceship.os.readBoolean
import it.scoppelletti.spaceship.os.writeBoolean
import mu.KotlinLogging

/**
 * Circular indeterminate progress indicator within a screen overlay.
 *
 * @since 1.0.0
 */
@UiThread
public class ProgressOverlay : FrameLayout {
    private val indicator: ProgressBar
    private val showAnim: ValueAnimator
    private val hideAnim: ValueAnimator

    /**
     * @constructor       Constructor.
     * @param       ctx   The context.
     */
    public constructor(ctx: Context) : super(ctx)

    /**
     * @constructor       Constructor.
     * @param       ctx   The context.
     * @param       attrs The attributes.
     */
    public constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)

    init {
        val layout: FrameLayout.LayoutParams

        indicator = ProgressBar(context)
        indicator.isIndeterminate = true
        layout = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        addView(indicator, layout)

        showAnim = initShowAnimator()
        hideAnim = initHideAnimator()
    }

    /**
     * Creates the animation for showing this progress indicator.
     *
     * @return The new object.
     */
    private fun initShowAnimator(): ValueAnimator {
        val anim = ValueAnimator.ofInt(ProgressOverlay.ALPHA_GONE,
                ProgressOverlay.ALPHA_VISIBLE)
        anim.startDelay = ProgressOverlay.DELAY
        anim.duration = ProgressOverlay.DURATION

        anim.addUpdateListener { animation ->
            val color: Int
            val f: Float

            f = animation.animatedFraction
            indicator.scaleX = f
            indicator.scaleY = f

            color = Color.argb(animation.animatedValue as Int, 0, 0, 0)
            setBackgroundColor(color)
        }

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
                setRunning(false)
            }
        })

        return anim
    }

    /**
     * Creates the animation for hiding this progress indicator.
     *
     * @return The new object.
     */
    private fun initHideAnimator(): ValueAnimator {
        val anim = ValueAnimator.ofInt(ProgressOverlay.ALPHA_VISIBLE,
                ProgressOverlay.ALPHA_GONE)
        anim.duration = ProgressOverlay.DURATION

        anim.addUpdateListener { animation ->
            val color: Int
            val f: Float

            f = 1.0f - animation.animatedFraction
            indicator.scaleX = f
            indicator.scaleY = f

            color = Color.argb(animation.animatedValue as Int, 0, 0, 0)
            setBackgroundColor(color)
        }

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
                setRunning(true)
            }
        })

        return anim
    }

    /**
     * Sets whether this progress indicator is running or not.
     *
     * @param running Whether this progress indicator is running.
     */
    private fun setRunning(running: Boolean) {
        val color: Int

        if (running) {
            color = Color.argb(ProgressOverlay.ALPHA_VISIBLE, 0, 0, 0)
            setBackgroundColor(color)
            indicator.scaleX = 1.0f
            indicator.scaleY = 1.0f
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
            color = Color.argb(ProgressOverlay.ALPHA_GONE, 0, 0, 0)
            setBackgroundColor(color)
            indicator.scaleX = 0.0f
            indicator.scaleY = 0.0f
        }
    }

    /**
     * Shows this progress indicator.
     */
    public fun show() {
        if (hideAnim.isStarted) {
            try {
                hideAnim.cancel()
            } catch (ex: RuntimeException) {
                // This is a problem, but I don't know what to do, yet.
                logger.error("Failed to cancel animation.", ex)
            }

            return
        }

        if (showAnim.isStarted || visibility == View.VISIBLE) {
            // Already shown
            return
        }

        showAnim.start()
    }

    /**
     * Hides this progress indicator.
     *
     * @param postAction The action to run when the animation finishes.
     */
    public fun hide(postAction: () -> Unit = { }) {
        if (doHide()) {
            hideAnim.addListener(createHideListener(postAction))
            hideAnim.start()
        } else {
            postAction()
        }
    }

    /**
     * Hides this progress indicator.
     *
     * @return Returns `true` if hide animation should start, `false` otherwise.
     */
    private fun doHide(): Boolean {
        if (showAnim.isStarted) {
            try {
                showAnim.cancel()
            } catch (ex: RuntimeException) {
                logger.error("Failed to cancel animation.", ex)
            }

            return false
        }

        if (hideAnim.isStarted || visibility != View.VISIBLE) {
            // Already hidden
            return false
        }

        hideAnim.start()
        return true
    }

    /**
     * Creates a listener for the hiding animation.
     *
     * @param  postAction The action to run when the animation finishes.
     * @return            The new object.
     */
    private fun createHideListener(
            postAction: () -> Unit
    ): Animator.AnimatorListener {
        return object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                hideAnim.removeListener(this)
                postAction()
            }
        }
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

    override fun onSaveInstanceState(): Parcelable {
        val running: Boolean
        val state: ProgressOverlay.SavedState

        state = ProgressOverlay.SavedState(super.onSaveInstanceState())

        running = when {
            showAnim.isStarted -> true
            hideAnim.isStarted -> false
            else -> (visibility == View.VISIBLE)
        }

        state.running = running
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is ProgressOverlay.SavedState) {
            super.onRestoreInstanceState(state.superState)
            setRunning(state.running)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        private const val ALPHA_GONE: Int = 0
        private const val ALPHA_VISIBLE: Int = 102
        private const val DELAY: Long = 400L
        private const val DURATION: Long = 400L
        private val logger = KotlinLogging.logger {}
    }

    /**
     * Component state.
     */
    public class SavedState : View.BaseSavedState {

        /**
         * Indicates whether the progress indicator is running or not.
         */
        public var running: Boolean = false

        /**
         * @constructor        Constructor.
         * @param       source The input stream.
         */
        public constructor(source: Parcelable) : super(source)

        /**
         * @constructor        Constructor.
         * @param       source The input stream.
         */
        private constructor(source: Parcel) : super(source) {
            running = source.readBoolean()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeBoolean(running)
        }

        companion object {

            /**
             * The `Parcelable` support.
             */
            @JvmField
            public val CREATOR = parcelableCreator(::SavedState)
        }
    }
}
