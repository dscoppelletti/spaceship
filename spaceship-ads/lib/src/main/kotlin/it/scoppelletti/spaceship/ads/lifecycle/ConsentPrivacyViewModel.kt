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
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.fromHtml
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel of the `ConsentPrivacyFragment` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
 * @since 1.0.0
 *
 * @property state State of the view.
 */
public class ConsentPrivacyViewModel @Inject constructor(

        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler,

        @Named(StdlibExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val scope = CoroutineScope(dispatcher + Job())
    private val _state = MutableLiveData<ConsentPrivacyState>()

    public val state: LiveData<ConsentPrivacyState> = _state

    /**
     * Builds the displayable styled text from the provided HTML strings.
     *
     * @param header Source HTML string for the header.
     * @param footer Source HTML string for the footer.
     */
    public fun buildText(header: String, footer: String) = scope.launch {
        val h: CharSequence
        val f: CharSequence

        if (_state.value != null) {
            return@launch
        }

        h = try {
            fromHtml(header, null, tagHandler)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Failed to build header text.", ex)
            SpannedString.valueOf(ex.localizedMessage)
        }

        f = try {
            fromHtml(footer, null, tagHandler)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Failed to build footer text.", ex)
            SpannedString.valueOf(ex.localizedMessage)
        }

        _state.value = ConsentPrivacyState(h, f)
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}

/**
 * State of the `ConsentPrivacyFragment` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
 * @since 1.0.0
 *
 * @property header Header text.
 * @property footer Footer text.
 */
public data class ConsentPrivacyState(
        public val header: CharSequence,
        public val footer: CharSequence
)
