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

package it.scoppelletti.spaceship.html.inject

import android.text.Editable
import android.text.Html
import android.text.Spanned
import it.scoppelletti.spaceship.html.HtmlTagHandler
import mu.KotlinLogging
import org.xml.sax.XMLReader
import javax.inject.Inject
import javax.inject.Provider

/**
 * Implementation of `Html.TagHandler` interface for delegating the HTML custom
 * tags to the injected `HtmlTagHandler` implementations.
 *
 * @since 1.0.0
 */
public class InjectHtmlTagHandler @Inject constructor(
        private val creators: Map<String,
                @JvmSuppressWildcards Provider<HtmlTagHandler>>
) : Html.TagHandler {

    override fun handleTag(
            opening: Boolean,
            tag: String?,
            output: Editable?,
            xmlReader: XMLReader?
    ) {
        val start: Int
        val handler: HtmlTagHandler
        val openHandler: HtmlTagHandler?
        val creator: Provider<HtmlTagHandler>?

        if (tag.isNullOrBlank()) {
            throw NullPointerException("Argument tag is null.")
        }
        if (output == null) {
            throw NullPointerException("Argument output is null.")
        }
        if (xmlReader == null) {
            throw NullPointerException("Argument xmlReader is null.")
        }

        if (opening) {
            creator = creators.entries.firstOrNull { tag.equals(it.key, true) }
                    ?.value
            if (creator == null) {
                logger.warn { "No HtmlTagHandler found for tag $tag." }
                return
            }

            handler = creator.get()
            if (!handler.tag.equals(tag, true)) {
                logger.error {
                    "HtmlTagHandler has tag ${handler.tag} instead of $tag."
                }

                return
            }

            output.setSpan(handler, output.length, output.length,
                    Spanned.SPAN_MARK_MARK)
        } else {
            openHandler = getLastTag(output, tag)
            if (openHandler == null) {
                logger.error { "No open tag found for tag $tag." }
                return
            }

            start = output.getSpanStart(openHandler)
            output.removeSpan(openHandler)
            openHandler.handleTag(output, start, output.length, xmlReader)
        }
    }

    /**
     * Returns the open tag corresponding to a close tag.
     *
     * @param  text Building text.
     * @param  tag  Tag to look for.
     * @return      Tag handler marking the open tag. If no open tag is found,
     *              returns `null`.
     */
    private fun getLastTag(text: Editable, tag: String?): HtmlTagHandler? {
        val span: Array<HtmlTagHandler>

        span = text.getSpans(0, text.length, HtmlTagHandler::class.java)
        return span.findLast { it.tag.equals(tag, true) &&
                text.getSpanFlags(it) == Spanned.SPAN_MARK_MARK }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}


