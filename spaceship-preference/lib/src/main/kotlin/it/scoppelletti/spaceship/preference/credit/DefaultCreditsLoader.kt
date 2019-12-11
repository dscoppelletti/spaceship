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

@file:Suppress("JoinDeclarationAndAssignment", "RemoveRedundantQualifierName",
        "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.preference.credit

import android.content.res.Resources
import android.text.Html
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.fromHtml
import it.scoppelletti.spaceship.preference.i18n.PreferenceMessages
import it.scoppelletti.spaceship.preference.model.Credit
import it.scoppelletti.spaceship.types.StringExt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject
import javax.inject.Named

/**
 * Default implementation of the `CreditsLoader` interface.
 *
 * @since 1.0.0
 *
 * @constructor            Constructor.
 * @param       resources  Resources of this application.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class DefaultCreditsLoader @Inject constructor(
        private val resources: Resources,

        private val preferenceMessages: PreferenceMessages,

        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : CreditsLoader {

    override suspend fun load(creditId: Int): List<Credit> =
            withContext(Dispatchers.IO) {
                val parser: XmlPullParser
                val state: CreditsState
                var eventType: Int

                try {
                    parser = resources.getXml(creditId)
                } catch (ex: Resources.NotFoundException) {
                    throw ApplicationException(
                            preferenceMessages.errorCreditFailed(), ex)
                }

                state = CreditsState(mutableListOf(), parser)

                try {
                    eventType = parser.eventType
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (!isActive) {
                            break
                        }

                        onEvent(state, eventType)
                        eventType = parser.next()
                    }
                } catch (ex: CancellationException) {
                    throw ex
                } catch (ex: Exception) {
                    // IOException | XmlPullParserException
                    throw ApplicationException(
                            preferenceMessages.errorCreditFailed(), ex)
                }

                state.list
            }

    /**
     * Handles an event.
     *
     * @param state     State.
     * @param eventType Event type.
     */
    private suspend fun onEvent(state: CreditsState, eventType: Int) {
        when (eventType) {
            XmlPullParser.START_TAG ->
                onStartTag(state)
            XmlPullParser.END_TAG ->
                onEndTag(state)
            XmlPullParser.TEXT ->
                onText(state)
        }
    }

    /**
     * Handles a start tag.
     *
     * @param state State.
     */
    private fun onStartTag(state: CreditsState) {
        when (state.parser.name) {
            DefaultCreditsLoader.ELEMENT_CREDIT ->
                state.clear()
            DefaultCreditsLoader.ELEMENT_COMPONENT ->
                state.element = DefaultCreditsLoader.ELEMENT_COMPONENT
            DefaultCreditsLoader.ELEMENT_OWNER ->
                state.element = DefaultCreditsLoader.ELEMENT_OWNER
            DefaultCreditsLoader.ELEMENT_LICENSE ->
                state.element = DefaultCreditsLoader.ELEMENT_LICENSE
            else ->
                state.element = StringExt.EMPTY
        }
    }

    /**
     * Handles an end tag.
     *
     * @param state State.
     */
    private suspend fun onEndTag(state: CreditsState) {
        val credit: Credit

        when (state.parser.name) {
            DefaultCreditsLoader.ELEMENT_CREDIT -> {
                credit = state.toCredit(tagHandler)
                state.list.add(credit)
            }

            else ->
                state.element = StringExt.EMPTY
        }
    }

    /**
     * Handles a text.
     *
     * @param state State.
     */
    private fun onText(state: CreditsState) {
        val text: String

        text = state.parser.text
        when (state.element) {
            DefaultCreditsLoader.ELEMENT_COMPONENT ->
                state.component = text
            DefaultCreditsLoader.ELEMENT_OWNER ->
                state.owner = text
            DefaultCreditsLoader.ELEMENT_LICENSE ->
                state.license = text
        }

        state.element = StringExt.EMPTY
    }

    public companion object {

        /**
         * Element `<credit>`.
         */
        public const val ELEMENT_CREDIT = "credit"

        /**
         * Element `<component>`.
         */
        public const val ELEMENT_COMPONENT = "component"

        /**
         * Element `<owner>`.
         */
        public const val ELEMENT_OWNER = "owner"

        /**
         * Element `<license>`.
         */
        public const val ELEMENT_LICENSE = "license"
    }
}

/**
 * State of the process.
 *
 * @property parser    XML parser.
 * @property element   Current element name.
 * @property component Component.
 * @property owner     Owner.
 * @property license   License.
 */
private data class CreditsState(
        val list: MutableList<Credit>,
        val parser: XmlPullParser,
        var element: String = StringExt.EMPTY,
        var component: String = StringExt.EMPTY,
        var owner: String = StringExt.EMPTY,
        var license: String = StringExt.EMPTY
) {

    /**
     * Builds the credit.
     *
     * @param  tagHandler Handles the HTML custom tags.
     * @return            The new object.
     */
    suspend fun toCredit(tagHandler: Html.TagHandler): Credit {
        val credit: Credit

        credit = Credit(fromHtml(component, null, tagHandler),
                fromHtml(owner, null, tagHandler),
                fromHtml(license, null, tagHandler))
        clear()
        return credit
    }

    /**
     * Clears the state.
     */
    fun clear() {
        element = StringExt.EMPTY
        component = StringExt.EMPTY
        owner = StringExt.EMPTY
        license = StringExt.EMPTY
    }
}
