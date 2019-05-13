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
 * limitations under the License.
 */

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.ads.lifecycle

import android.text.Html
import android.text.SpannedString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.fromHtml
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel of the `AbstractConsentActivity` fragments.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment
 * @see   it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
 * @since 1.0.0
 *
 * @property text Message text.
 *
 * @constructor            Constructor.
 * @param       dispatcher Coroutine dispatcher.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class ConsentFragmentViewModel @Inject constructor(

        @Named(CoreExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModel(), CoroutineScope {

    private val _text = MutableLiveData<CharSequence>()
    private val job = Job()

    override val coroutineContext: CoroutineContext = dispatcher + job

    public val text: LiveData<CharSequence> = _text

    /**
     * Builds the displayable styled text from the provided HTML string.
     *
     * @param source Source HTML string.
     */
    public fun buildText(source: String) = launch {
        val text: CharSequence

        if (_text.value != null) {
            return@launch
        }

        try {
            text = fromHtml(source, null, tagHandler)
            _text.value = text
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Failed to build text.", ex)
            _text.value = SpannedString.valueOf(ex.localizedMessage)
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
