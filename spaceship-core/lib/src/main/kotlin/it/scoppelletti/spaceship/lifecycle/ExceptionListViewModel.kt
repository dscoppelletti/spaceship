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

package it.scoppelletti.spaceship.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.ExceptionLogger
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import mu.KLogger
import mu.KotlinLogging
import java.lang.Exception
import javax.inject.Inject

/**
 * `ViewModel` of an exception chain.
 *
 * @since 1.0.0
 *
 * @property outerEx Exception at the head of the chain.
 * @property state   Collection of exceptions.
 *
 * @constructor                  Constructor.
 * @params      adapterFactory   Creates an adapter for an exception class.
 * @params      exceptionLoggers Logs an exception.
 */
@JvmSuppressWildcards
public class ExceptionListViewModel @Inject constructor(
        private val adapterFactory: ExceptionAdapter.Factory,
        private val exceptionLoggers: Set<ExceptionLogger>
): ViewModel() {

    public var outerEx: Throwable?
    private val _state: MutableLiveData<ExceptionListState>
    private val disposables: CompositeDisposable

    public val state: LiveData<ExceptionListState>
        get() = _state

    init {
        outerEx = null
        _state = MutableLiveData()
        disposables = CompositeDisposable()
    }

    /**
     * Loads the chain of an exception.
     */
    public fun load() {
        val subscription: Disposable

        if (_state.value?.exList?.isNotEmpty() == true) {
            return
        }

        subscription = Observable.create<Throwable> generator@{ emitter ->
            outerEx?.let { ex ->
                exceptionLoggers.forEach {
                    if (emitter.isDisposed) {
                        return@generator
                    }

                    try {
                        it.log(ex)
                    } catch (logEx: Exception) {
                        logger.error(logEx) { "Failure in logger $it." }
                    }
                }
            }

            var ex: Throwable? = outerEx

            while (ex != null) {
                if (emitter.isDisposed) {
                    return@generator
                }

                emitter.onNext(ex)
                ex = ex.cause
            }

            emitter.onComplete()
        }
                .map { ex ->
                    val adapter: ExceptionAdapter<*>

                    adapter = adapterFactory.create(ex.javaClass)
                    ExceptionItemState(ex, adapter)
                }
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { exList ->
                    _state.value = ExceptionListState(exList)
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
 * State of an exception chain.
 *
 * @since 1.0.0
 *
 * @property exList Collection of exceptions.
 *
 * @constructor Constructor.
 */
public data class ExceptionListState(
        public val exList: List<ExceptionItemState>
)

/**
 * State of an exception.
 *
 * @since 1.0.0
 *
 * @property ex      Exception.
 * @property adapter Renders an exception as an item in a `ListView` control.
 *
 * @constructor Constructor.
 */
public data class ExceptionItemState(
        public val ex: Throwable,
        public val adapter: ExceptionAdapter<*>
)

