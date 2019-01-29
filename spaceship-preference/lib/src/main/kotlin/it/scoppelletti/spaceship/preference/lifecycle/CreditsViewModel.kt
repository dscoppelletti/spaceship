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

package it.scoppelletti.spaceship.preference.lifecycle

import androidx.annotation.XmlRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.preference.credit.CreditsLoader
import it.scoppelletti.spaceship.preference.model.Credit
import javax.inject.Inject

/**
 * ViewModel of the `CreditsActivity` view.
 *
 * @see   it.scoppelletti.spaceship.preference.CreditsActivity
 * @since 1.0.0
 *
 * @property state State of the view.
 *
 * @constructor        Constructor.
 * @param       loader Loads the credits.
 */
public class CreditsViewModel @Inject constructor(
        private val loader: CreditsLoader
): ViewModel() {

    private val _state: MutableLiveData<CreditsState>
    private val disposables: CompositeDisposable

    public val state: LiveData<CreditsState>
        get() = _state

    init {
        _state = MutableLiveData()
        disposables = CompositeDisposable()
    }

    /**
     * Loads the credits.
     *
     * @param creditId ID of the XML resource.
     */
    public fun load(@XmlRes creditId: Int) {
        val subscription: Disposable

        if (_state.value != null) {
            return
        }

        _state.value = CreditsState(emptyList(), true, null)

        subscription = loader.load(creditId)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    _state.value = CreditsState.create(items)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}

/**
 * State of the `CreditsActivity` view.
 *
 * @see   it.scoppelletti.spaceship.preference.CreditsActivity
 * @since 1.0.0
 *
 * @property items   Collection of credits.
 * @property waiting Whether a work is in progress.
 * @property error   Error to show.
 *
 * @constructor Constructor.
 */
public data class CreditsState(
        public val items: List<Credit>,
        val waiting: Boolean,
        val error: SingleEvent<Throwable>?
) {

    /**
     * Sets an error to show.
     *
     * @param  ex The error.
     * @return    The new object.
     */
    internal fun withError(ex: Throwable): CreditsState =
            copy(error = SingleEvent(ex), waiting = false)

    internal companion object {

        /**
         * Creates an instance.
         *
         * @param  items Collection of credits.
         * @return       The new object.
         */
        internal fun create(items: List<Credit>): CreditsState =
                CreditsState(items, false, null)
    }
}
