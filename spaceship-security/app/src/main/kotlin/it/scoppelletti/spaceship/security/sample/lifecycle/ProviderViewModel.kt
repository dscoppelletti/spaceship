package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.scoppelletti.spaceship.html.fromHtml
import it.scoppelletti.spaceship.types.StringExt
import java.security.Security
import javax.inject.Inject

class ProviderViewModel @Inject constructor() : ViewModel() {
    private val _state: MutableLiveData<CharSequence>
    private val disposables: CompositeDisposable

    val state: LiveData<CharSequence>
        get() = _state

    init {
        _state = MutableLiveData()
        disposables = CompositeDisposable()
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    fun load() {
        val subscription: Disposable

        subscription = Observable.create<Algorithm> { emitter ->
            onLoadSubscribe(emitter)
        }
                .sorted()
                .collectInto(AlgorithmCollector()) { collector, alg ->
                    collector.collect(alg)
                }
                .map {
                    fromHtml(it.toString(), null, null)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { text ->
                    _state.value = text
                }
        disposables.add(subscription)
    }

    private fun onLoadSubscribe(emitter: ObservableEmitter<Algorithm>) {
        Security.getProviders().forEach { provider ->
            if (emitter.isDisposed) {
                return
            }

            provider.services.forEach { service ->
                if (emitter.isDisposed) {
                    return
                }

                emitter.onNext(Algorithm(provider.name, service.type,
                        service.algorithm))
            }
        }

        emitter.onComplete()
    }
}

private data class Algorithm(
        val provider: String,
        val service: String,
        val algorithm: String
) : Comparable<Algorithm> {

    override fun compareTo(other: Algorithm): Int {
        var cmp: Int

        cmp = provider.compareTo(other.provider)
        if (cmp != 0) {
            return cmp
        }

        cmp = service.compareTo(other.service)
        if (cmp != 0) {
            return cmp
        }

        cmp = algorithm.compareTo(other.algorithm)
        if (cmp != 0) {
            return cmp
        }

        return 0
    }
}

private class AlgorithmCollector {
    private val buf: StringBuilder
    private var provider: String
    private var service: String

    init {
        buf = StringBuilder()
        provider = StringExt.EMPTY
        service = StringExt.EMPTY
    }

    fun collect(alg: Algorithm) {
        if (alg.provider != provider) {
            buf.append("<h1>Provider ")
                    .append(alg.provider)
                    .append("</h1>")
            provider = alg.provider
            service = StringExt.EMPTY
        }

        if (alg.service != service) {
            buf.append("<h2>Service ")
                    .append(alg.service)
                    .append("</h2>")
            service = alg.service
        }

        buf.append(alg.algorithm)
                .append("<br>")
    }

    override fun toString(): String = buf.toString()
}