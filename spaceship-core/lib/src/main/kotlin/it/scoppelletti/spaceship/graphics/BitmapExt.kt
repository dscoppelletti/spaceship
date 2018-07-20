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

package it.scoppelletti.spaceship.graphics

import android.graphics.BitmapFactory

/**
 * Calculates the sample size (`inSampleSize`) for loading a subsampled version
 * of an image.
 *
 * * [Loading Large Bitmaps Efficiently](http://developer.android.com/topic/performance/graphics/load-bitmap)
 *
 * @receiver            Decoder options for loading the image. The size of the
 *                      original image (`outWidth`, `outHeight`) must be preset.
 * @param    viewWidth  Width of the target image container.
 * @param    viewHeight Height of the target image container.
 * @since    1.0.0
 */
public fun BitmapFactory.Options.calculateInSampleSize(
        viewWidth: Int,
        viewHeight: Int
) {
    val width2: Int
    val height2: Int

    if (this.outWidth <= 0 || this.outHeight <= 0) {
        throw IllegalArgumentException(
                "Original size is not properly determinated.")
    }
    if (viewWidth <= 0) {
        throw IllegalArgumentException(
                "Argument viewWidth must be greater than 0.")
    }
    if (viewHeight <= 0) {
        throw IllegalArgumentException(
                "Argument viewHeight must be greater than 0.")
    }

    this.inSampleSize = 1
    if (this.outWidth <= viewWidth && this.outHeight <= viewHeight) {
        return
    }

    width2 = viewWidth / 2
    height2 = viewHeight / 2

    // Powers of 2 are better
    while (width2 / this.inSampleSize >= viewWidth &&
            height2 / this.inSampleSize >= viewHeight) {
        this.inSampleSize *= 2
    }
}
