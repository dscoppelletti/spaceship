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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
import it.scoppelletti.spaceship.ads.app.ConsentLoadFragment
import it.scoppelletti.spaceship.ads.consent.ConsentDataLoader
import it.scoppelletti.spaceship.ads.consent.ConsentDataStore
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.model.ConsentData
import javax.inject.Inject

/**
 * ViewModel of the `AbstractConsentActivity` view.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @see   it.scoppelletti.spaceship.ads.model.ConsentData
 * @since 1.0.0
 *
 * @property state State of the view.
 *
 * @constructor                   Constructor.
 * @param       consentDataStore  Local store for the `ContextData` object.
 * @param       consentDataLoader Loader of the current `ConsentData` object.
 */
public class ConsentViewModel @Inject constructor(
        private val consentDataStore: ConsentDataStore,
        private val consentDataLoader: ConsentDataLoader
) : ViewModel() {
    private val _state: MutableLiveData<ConsentState>
    private val disposables: CompositeDisposable

    public val state: LiveData<ConsentState>
        get() = _state

    init {
        _state = MutableLiveData()
        disposables = CompositeDisposable()
    }

    /**
     * Loads the `ConsentData` object.
     */
    public fun load() {
        val subscription: Disposable

        if (_state.value != null) {
            return
        }

        _state.value = ConsentState(step = ConsentLoadFragment.POS,
                data = ConsentData(), saved = false, waiting = true,
                error = null)

        subscription = consentDataLoader.load()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    _state.value = _state.value?.copy(step =
                        ConsentAgeFragment.POS, data = data, waiting = false)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    /**
     * Saves the consent status.
     *
     * @param status The consent status.
     */
    public fun save(status: ConsentStatus) {
        val data: ConsentData
        val subscription: Disposable

        if (_state.value == null) {
            return
        }

        _state.value = _state.value?.copy(waiting = true)

        data = (_state.value?.data ?: ConsentData())
                .copy(consentStatus = status)
        subscription = consentDataStore.save(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _state.value = _state.value?.copy(data = data,
                            saved = true, waiting = false)
                }, { ex ->
                    _state.value = _state.value?.withError(ex)
                })
        disposables.add(subscription)
    }

    /**
     * Moves to a fragment.
     *
     * @param value Position of the fragment to select.
     */
    public fun setStep(value: Int) {
        _state.value = _state.value?.copy(step = value, waiting = false)
    }

    /**
     * Moves to the previous fragment.
     *
     * @return Returns `true` if the method succeeded, `false` otherwise.
     */
    public fun backStep(): Boolean {
        try {
            _state.value = _state.value?.backStep()
        } catch (ex: IllegalStateException) {
            return false
        }

        return true
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}