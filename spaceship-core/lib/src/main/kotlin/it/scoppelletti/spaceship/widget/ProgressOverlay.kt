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
import android.os.Build
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
import it.scoppelletti.spaceship.os.readBoolean
import it.scoppelletti.spaceship.os.writeBoolean
import mu.KLogger
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
     * @constructor     Constructor.
     * @param       ctx Context.
     */
    public constructor(ctx: Context) : super(ctx)

    /**
     * @constructor       Constructor.
     * @param       ctx   Context.
     * @param       attrs Attributes.
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

        showAnim = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            initShowAnimator24() else initShowAnimator()
        hideAnim = initHideAnimator()
    }

    /**
     * Creates the animation for showing this progress indicator.
     *
     * @return The new object.
     */
    private fun initShowAnimator(): ValueAnimator {
        val anim: ValueAnimator

        anim = ValueAnimator.ofInt(ProgressOverlay.ALPHA_GONE,
                ProgressOverlay.ALPHA_VISIBLE).apply {
            startDelay = ProgressOverlay.DELAY
            duration = ProgressOverlay.DURATION
        }

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
                logger.trace("Show animation start.")
                visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                logger.trace("Show animation end.")
            }

            override fun onAnimationCancel(animation: Animator?) {
                logger.trace("Show animation cancel.")
                setRunning(false)
            }
        })

        return anim
    }

    /**
     * Creates the animation for showing this progress indicator.
     *
     * @return The new object.
     */
    private fun initShowAnimator24(): ValueAnimator {
        // - Android 7.0, API 24
        // Sometime the cancel method doesn't have any effect maybe because the
        // delay phase is not completed:
        // Implement the delay phase in the animation phase.
        val anim: ValueAnimator
        val durationFrac: Float
        val delayFrac: Float

        anim = ValueAnimator.ofInt(ProgressOverlay.ALPHA_GONE,
                ProgressOverlay.ALPHA_VISIBLE).apply {
            duration = ProgressOverlay.DELAY + ProgressOverlay.DURATION
        }

        delayFrac = ProgressOverlay.DELAY.toFloat() / anim.duration.toFloat()
        durationFrac = anim.duration.toFloat() /
                ProgressOverlay.DURATION.toFloat()

        anim.addUpdateListener { animation ->
            val color: Int
            val alpha: Float
            val f: Float
            val scale: Float

            f = animation.animatedFraction
            if (f >= delayFrac) {
                if (visibility != View.VISIBLE) {
                    visibility = View.VISIBLE
                }

                scale = (f - delayFrac) * durationFrac
                indicator.scaleX = scale
                indicator.scaleY = scale

                alpha = ProgressOverlay.ALPHA_VISIBLE.toFloat() * scale
                color = Color.argb(alpha.toInt(), 0, 0, 0)
                setBackgroundColor(color)
            }
        }

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                logger.trace("Show animation start.")
            }

            override fun onAnimationEnd(animation: Animator?) {
                logger.trace("Show animation end.")
            }

            override fun onAnimationCancel(animation: Animator?) {
                logger.trace("Show animation cancel.")
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
                ProgressOverlay.ALPHA_GONE).apply {
            duration = ProgressOverlay.DURATION
        }

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
            override fun onAnimationStart(animation: Animator?) {
                logger.trace("Hide animation start.")
            }

            override fun onAnimationEnd(animation: Animator?) {
                logger.trace("Hide animation end.")
                visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
                logger.trace("Hide animation cancel.")
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
                logger.trace("Cancel hide animation")
                hideAnim.cancel()
            } catch (ex: RuntimeException) {
                // This is a problem, but I don't know what to do, yet.
                logger.error("Failed to cancel hide animation.", ex)
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
                // - Android 7.0, API 24
                // Sometime the cancel method doesn't have any effect maybe
                // because the delay phase is not completed:
                // See differences between method initShowAnimator and
                // initShowAnimator24.
                logger.trace("Cancel show animation.")
                showAnim.cancel()
            } catch (ex: RuntimeException) {
                logger.error("Failed to cancel show animation.", ex)
            }

            return false
        }

        if (hideAnim.isStarted || visibility != View.VISIBLE) {
            // Already hidden
            return false
        }

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

    override fun onSaveInstanceState(): Parcelable? {
        val running: Boolean
        val superState: Parcelable?
        val state: ProgressOverlay.SavedState

        superState = super.onSaveInstanceState()
        if (superState == null) {
            return null
        }

        state = ProgressOverlay.SavedState(superState)

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

    private companion object {
        const val ALPHA_GONE: Int = 0
        const val ALPHA_VISIBLE: Int = 102
        const val DELAY: Long = 400L
        const val DURATION: Long = 400L
        val logger: KLogger = KotlinLogging.logger {}
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
         * @param       source Input stream.
         */
        public constructor(source: Parcelable) : super(source)

        /**
         * @constructor        Constructor.
         * @param       source Input stream.
         */
        private constructor(source: Parcel) : super(source) {
            running = source.readBoolean()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeBoolean(running)
        }

        public companion object {

            /**
             * The `Parcelable` support.
             */
            @JvmField
            public val CREATOR = parcelableCreator(::SavedState)
        }
    }
}
