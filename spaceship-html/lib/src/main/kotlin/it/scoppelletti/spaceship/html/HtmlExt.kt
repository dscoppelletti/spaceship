/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
 * limit
 */

package it.scoppelletti.spaceship.html

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * Operations for HTML.
 *
 * @since 1.0.0
 */
public object HtmlExt {

    /**
     * Name of the `Html.TagHandler` dependency.
     */
    public const val DEP_TAGHANDLER: String = "it.scoppelletti.spaceship.html.1"
}

private const val SPAN_START: String = "<span>"
private const val SPAN_END: String = "</span>"

/**
 * Returns a displayable styled text from the provided HTML string.
 *
 * @param  source      The source HTML string.
 * @param  imageGetter Provides the representation of the image for an
 *                    `<IMG>` tag.
 * @param  tagHandler  Handles an unknown tag.
 * @return             The resulting styled text.
 * @since              1.0.0
 */
@Suppress("deprecation")
public fun fromHtml(
        source: String,
        imageGetter: Html.ImageGetter? = null,
        tagHandler: Html.TagHandler? = null
) : Spanned {
    val html: String

    // - Android 8.1
    // If the source text starts with a custom tag, then the end of that tag is
    // not detected and it is assumed at the end of the source text:
    // Enclose the source text in a span element.
    html = StringBuilder(SPAN_START)
            .append(source)
            .append(SPAN_END).toString()

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        Html.fromHtml(html, imageGetter, tagHandler)
    else
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter,
                tagHandler)
}