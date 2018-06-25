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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Build
import android.text.Html
import android.text.Spanned
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import javax.inject.Inject

/**
 * `ViewModel` for the `HtmlViewerActivity` activity.
 *
 * @see            it.scoppelletti.spaceship.html.app.HtmlViewerActivity
 * @since          1.0.0
 * @property state The styled text.
 *
 * @constructor            Constructor.
 * @param       tagHandler Handles the HTML custom tags.
 */
public class HtmlViewerViewModel @Inject constructor(
        private val tagHandler: Html.TagHandler
) : ViewModel() {
    private val _state: MutableLiveData<HtmlViewerState> = MutableLiveData()
    private val disposables: CompositeDisposable = CompositeDisposable()

    public val state: LiveData<HtmlViewerState>
        get() = _state

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

    /**
     * Returns a displayable styled text from the provided HTML string.
     *
     * @param  source      The source HTML string.
     * @param  imageGetter Provides the representation of the image for an
     *                    `<IMG>` tag.
     * @param  tagHandler  Handles an unknown tag.
     * @return             The resulting styled text.
     */
    @Suppress("deprecation")
    private fun fromHtml(source: String,
                         imageGetter: Html.ImageGetter? = null,
                         tagHandler: Html.TagHandler? = null
    ) : Spanned =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                Html.fromHtml(source, imageGetter, tagHandler)
            else
                Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY, imageGetter,
                        tagHandler)

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
