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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.html

import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Operations for HTML.
 *
 * @since 1.0.0
 */
public object HtmlExt {

    /**
     * Name of the `Html.TagHandler` dependency.
     */
    public const val DEP_TAGHANDLER = "it.scoppelletti.spaceship.html.2"

    /**
     * Property indicating whether `home` should be displayed as an `up`
     * affordance.
     */
    public const val PROP_HOMEASUP = "it.scoppelletti.spaceship.html.1"

    /**
     * Property containing an HTML text as a string resource ID.
     */
    public const val PROP_HTML = "it.scoppelletti.spaceship.html.2"

    /**
     * Property containing a text.
     */
    public const val PROP_TEXT = "it.scoppelletti.spaceship.html.3"

    /**
     * Property containing a title as a string resource ID.
     */
    public const val PROP_TITLE = "it.scoppelletti.spaceship.html.4"
}

private const val SPAN_START = "<span>"
private const val SPAN_END = "</span>"

/**
 * Returns a displayable styled text from the provided HTML string.
 *
 * @param  source      Source HTML string.
 * @param  imageGetter Provides the representation of the image for an
 *                    `<IMG>` tag.
 * @param  tagHandler  Handles an unknown tag.
 * @return             Resulting styled text.
 * @since              1.0.0
 */
@Suppress("deprecation")
public suspend fun fromHtml(
        source: String,
        imageGetter: Html.ImageGetter? = null,
        tagHandler: Html.TagHandler? = null
) : Spanned = withContext(Dispatchers.Default) {
    val html: String

    // - Android 8.1
    // If the source text starts with a custom tag, then the end of that tag is
    // not detected and it is assumed at the end of the source text:
    // Enclose the source text in a span element.
    html = StringBuilder(SPAN_START)
            .append(source)
            .append(SPAN_END).toString()

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        Html.fromHtml(html, imageGetter, tagHandler)
    else
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter,
                tagHandler)
}

/**
 * Sets a custom handler for hyperlinks in a styled text.
 *
 * @receiver         Original styled text.
 * @param    onClick Custom handler.
 * @param    filter  Predicate to select the hyperlinks.
 * @return           Resulting styled text.
 * @since            1.0.0
 */
@Suppress("unused")
public suspend fun Spanned.replaceHyperlinks(
        onClick: (String) -> Unit,
        filter: ((String) -> Boolean)?
): Spanned = withContext(Dispatchers.Default) {
    SpannableStringBuilder(this@replaceHyperlinks)
            .apply {
                getSpans(0, this.length, URLSpan::class.java)
                        .filter {
                            filter?.invoke(it.url) ?: true
                        }
                        .forEach {
                            replaceHyperlink(it, onClick)
                        }
            }
}

/**
 * Sets a custom handler for an hyperlink in a styled text.
 *
 * @receiver         Styled text.
 * @param    urlSpan Hyperlink.
 * @param    onClick Custom handler.
 */
private fun SpannableStringBuilder.replaceHyperlink(
        urlSpan: URLSpan,
        onClick: (String) -> Unit
) {
    val start: Int
    val end: Int
    val flags: Int
    val newSpan: ClickableSpan

    start = this.getSpanStart(urlSpan)
    end = this.getSpanEnd(urlSpan)
    flags = this.getSpanFlags(urlSpan)
    newSpan = object : ClickableSpan() {

        override fun onClick(widget: View) {
            onClick(urlSpan.url)
        }
    }

    this.setSpan(newSpan, start, end, flags)
    this.removeSpan(urlSpan)
}
