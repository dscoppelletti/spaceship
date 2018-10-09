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

package it.scoppelletti.spaceship.ads.lifecycle

import android.text.Html
import android.text.SpannedString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.fromHtml
import mu.KLogger
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
 *
 * @constructor            Constructor.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class ConsentPrivacyViewModel @Inject constructor(
        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModel() {
    private val _state: MutableLiveData<ConsentPrivacyState>
    private val disposables: CompositeDisposable

    public val state: LiveData<ConsentPrivacyState>
        get() = _state

    init {
        _state = MutableLiveData()
        disposables = CompositeDisposable()
    }

    /**
     * Builds the displayable styled text from the provided HTML strings.
     *
     * @param header Source HTML string for the header.
     * @param footer Source HTML string for the footer.
     */
    public fun buildText(header: String, footer: String) {
        val subscription: Disposable

        if (_state.value != null) {
            return
        }

        subscription = Observable.fromCallable {
            fromHtml(header, null, tagHandler)
        }.onErrorReturn { ex ->
            logger.error("Failed to build header text.", ex)
            SpannedString.valueOf(ex.localizedMessage)
        }.flatMap { h ->
            Observable.fromCallable {
                fromHtml(footer, null, tagHandler)
            }.onErrorReturn { ex ->
                logger.error("Failed to build footer text.", ex)
                SpannedString.valueOf(ex.localizedMessage)
            }.map { f ->
                Pair(h, f)
            }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (h, f) ->
                    _state.value = ConsentPrivacyState(h, f)
                }
        disposables.add(subscription)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    private companion object {
        val logger: KLogger = KotlinLogging.logger {}
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
