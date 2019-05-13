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

package it.scoppelletti.spaceship.html.lifecycle

import android.text.Html
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.fromHtml
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.types.StringExt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

/**
 * `ViewModel` for the `HtmlViewerActivity` activity.
 *
 * @see   it.scoppelletti.spaceship.html.app.HtmlViewerActivity
 * @since 1.0.0
 *
 * @property state The styled text.
 *
 * @constructor            Constructor.
 * @param       dispatcher Coroutine dispatcher.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class HtmlViewerViewModel @Inject constructor(

        @Named(CoreExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher,

        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModel(), CoroutineScope {

    private val _state = MutableLiveData<HtmlViewerState>()
    private val job = Job()

    override val coroutineContext: CoroutineContext = dispatcher + job

    public val state: LiveData<HtmlViewerState> = _state

    /**
     * Builds the displayable styled text from the provided HTML string.
     *
     * @param source The source HTML string.
     */
    public fun buildText(source: String) = launch {
        val text: CharSequence

        try {
            text = fromHtml(source, null, tagHandler)
            _state.value = HtmlViewerState(text = text)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = HtmlViewerState(error = SingleEvent(ex))
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

/**
 * State for the `HtmlViewerActivity` activity.
 *
 * @see    it.scoppelletti.spaceship.html.app.HtmlViewerActivity
 * @since  1.0.0
 *
 * @property text  The styled text.
 * @property error The error.
 */
public data class HtmlViewerState (
        public val text: CharSequence = StringExt.EMPTY,
        public val error: SingleEvent<Throwable>? = null
)
