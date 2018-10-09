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

package it.scoppelletti.spaceship.html.lifecycle

import android.text.Html
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
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import javax.inject.Inject
import javax.inject.Named

/**
 * `ViewModel` for the `HtmlViewerActivity` activity.
 *
 * @see   it.scoppelletti.spaceship.html.app.HtmlViewerActivity
 * @since 1.0.0
 *
 * @property state The styled text.
 *
 * @constructor            Constructor.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class HtmlViewerViewModel @Inject constructor(
        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModel() {

    private val _state: MutableLiveData<HtmlViewerState>
    private val disposables: CompositeDisposable

    public val state: LiveData<HtmlViewerState>
        get() = _state

    init {
        _state = MutableLiveData()
        disposables = CompositeDisposable()
    }

    /**
     * Builds the displayable styled text from the provided HTML string.
     *
     * @param source The source HTML string.
     */
    public fun buildText(source: String) {
        val subscription: Disposable

        subscription = Observable.fromCallable {
            fromHtml(source, null, tagHandler)
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ text ->
                    _state.value = HtmlViewerState(text = text)
                }, { ex ->
                    _state.value = HtmlViewerState(error = SingleEvent(ex))
                })
        disposables.add(subscription)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
