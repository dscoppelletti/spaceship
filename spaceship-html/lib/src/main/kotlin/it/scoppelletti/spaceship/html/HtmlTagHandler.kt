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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.html

import android.text.Editable
import org.xml.sax.XMLReader

/**
 * HTML custom tag handler.
 *
 * @since 1.0.0
 *
 * @property tag Custom tag.
 */
public abstract class HtmlTagHandler(public val tag: String) {

    /**
     * Handles a tag.
     *
     * @param output Text in building.
     * @param start  Start position of the tag in the text (inclusive).
     * @param end    End position of the tag in the text (exclusive).
     */
    public abstract fun handleTag(
            output: Editable,
            start: Int,
            end: Int,
            xmlReader: XMLReader)
}