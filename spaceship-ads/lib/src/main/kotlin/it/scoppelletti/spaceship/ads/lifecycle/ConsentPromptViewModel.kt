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
import it.scoppelletti.spaceship.html.replaceHyperlinks
import mu.KLogger
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel of the `ConsentPromptFragment` view.
 *
 * @see           it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
 * @since         1.0.0
 *
 * @property text Message text.
 *
 * @constructor            Constructor.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class ConsentPromptViewModel @Inject constructor(
        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModel() {
    private val _text: MutableLiveData<CharSequence>
    private val disposables: CompositeDisposable

    public val text: LiveData<CharSequence>
        get() = _text

    init {
        _text = MutableLiveData()
        disposables = CompositeDisposable()
    }

    /**
     * Builds the displayable styled text from the provided HTML string.
     *
     * @param source  The source HTML string.
     * @param url     URL of the privacy policy.
     * @param onClick Handles the click on the URL.
     */
    public fun buildText(
            source: String,
            url: String,
            onClick: (String) -> Unit) {
        val subscription: Disposable

        if (_text.value != null) {
            return
        }

        subscription = Observable.fromCallable {
            fromHtml(source, null, tagHandler)
        }.map { s ->
            s.replaceHyperlinks(onClick) {
                it == url
            }
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ s ->
                    _text.value = s
                }, { ex ->
                    logger.error("Failed to build text.", ex)
                    _text.value = SpannedString.valueOf(ex.localizedMessage)
                })
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
