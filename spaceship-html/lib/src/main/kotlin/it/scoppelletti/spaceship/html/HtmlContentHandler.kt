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

import mu.KotlinLogging
import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.XMLReader

private val logger = KotlinLogging.logger {}

/**
 * Implementation of the `ContentHandler` interface installed by the
 * [ContentHandlerTagHandler] tag.
 *
 * @property currentAttributes Collection of the attributes of the current HTML
 *                             tag.
 *
 * @constructor          Constructor.
 * @param       delegate Delegate implementation of the `ContentHandler`
 *                       interface.
 */
internal class HtmlContentHandler(
        private val delegate: ContentHandler
) : ContentHandler by delegate {
    private val attrStack: MutableList<Map<String, String>> = mutableListOf()

    var currentAttributes: Map<String, String> = emptyMap()
        private set

    override fun startElement(
            uri: String?,
            localName: String?,
            qName: String?,
            atts: Attributes?) {
        currentAttributes = if (atts != null) toMap(atts) else emptyMap()
        attrStack.add(currentAttributes)
        delegate.startElement(uri, localName, qName, atts)
    }

    /**
     * Converts an `Attributes` collection to a `Map` collection.
     *
     * @param  atts Original collection.
     * @return      Resulting collection.
     */
    private fun toMap(atts: Attributes): Map<String, String> {
        val len: Int
        val map: MutableMap<String, String>

        len = atts.length
        map = mutableMapOf()
        for (idx: Int in 0 until len) {
            map[atts.getLocalName(idx)] = atts.getValue(idx)
        }

        return map
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        if (attrStack.isEmpty()) {
            currentAttributes = emptyMap()
        } else {
            currentAttributes = attrStack.last()
            attrStack.removeAt(attrStack.lastIndex)
        }

        delegate.endElement(uri, localName, qName)
    }
}

/**
 * Returns the collection of the attributes of the current HTML tag.
 *
 * > This function properly works only for tags inserted after the
 * > `<it-scoppelletti-contentHandler/>` tag.
 *
 * @receiver `XMLReader` interface.
 * @return   Collection of attributes.
 * @see      it.scoppelletti.spaceship.html.ContentHandlerTagHandler
 * @since    1.0.0
 */
public fun XMLReader.getCurrentAttributes(): Map<String, String> {
    val contentHandler: HtmlContentHandler?

    contentHandler = this.contentHandler as? HtmlContentHandler
    if (contentHandler == null) {
        logger.error("Custom ContentHandler interface not installed.")
        return emptyMap()
    }

    return contentHandler.currentAttributes
}

