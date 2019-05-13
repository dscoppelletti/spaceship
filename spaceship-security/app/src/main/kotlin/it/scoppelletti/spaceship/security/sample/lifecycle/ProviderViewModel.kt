
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security.sample.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.html.fromHtml
import it.scoppelletti.spaceship.types.StringExt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.Security
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class ProviderViewModel @Inject constructor(

        @Named(CoreExt.DEP_MAINDISPATCHER)
        dispatcher: CoroutineDispatcher
) : ViewModel(), CoroutineScope {
    private val _state = MutableLiveData<CharSequence>()
    private val job = Job()

    override val coroutineContext: CoroutineContext = dispatcher + job

    val state: LiveData<CharSequence> = _state

    fun load() = launch {
        _state.value = loadProviders()
    }

    private suspend fun loadProviders(): CharSequence =
            withContext(Dispatchers.Default) {
                val collector = AlgorithmCollector()
                val list: MutableList<Algorithm> = mutableListOf()

                Security.getProviders().forEach { provider ->
                    if (!isActive) {
                        throw CancellationException()
                    }

                    provider.services.forEach { service ->
                        if (!isActive) {
                            throw CancellationException()
                        }

                        list.add(Algorithm(provider.name, service.type,
                                service.algorithm))
                    }
                }

                list.sorted().forEach {
                    if (!isActive) {
                        throw CancellationException()
                    }

                    collector.collect(it)
                }

                if (!isActive) {
                    throw CancellationException()
                }

                fromHtml(collector.toString(), null, null)
            }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
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
    private val buf = StringBuilder()
    private var provider: String = StringExt.EMPTY
    private var service: String = StringExt.EMPTY

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