package it.scoppelletti.spaceship.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

class CoroutineSpy(dispatcher: CoroutineDispatcher) : CoroutineScope {

    val job = Job()
    var finallySpy: Boolean = false
    var finally1Spy: Boolean = false
    var finally2Spy: Boolean = false
    var catchSpy: Throwable? = null

    override val coroutineContext: CoroutineContext = dispatcher + job +
            CoroutineName("CoroutineTest")

    fun dispose() {
        job.cancel()
    }

    fun childJob(timeMillis: Long) = launch(CoroutineName("childJob")) {
        println(Thread.currentThread().name)
        delay(timeMillis) // Delay the job completion to be able to check
            // whether this job is a child of the instance job.
    }

    fun sequentialFail() = launch(CoroutineName("sequentialFail")) {
        println(Thread.currentThread().name)

        try {
            throw RuntimeException("MyError")
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    fun sequentialCancel() = launch(CoroutineName("sequentialCancel")) {
        println(Thread.currentThread().name)

        try {
            throw CancellationException("MyCancel")
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    fun asyncFail() = launch(CoroutineName("asyncFail")) {
        println(Thread.currentThread().name)

        try {
            withContext<Unit>(Dispatchers.Default) {
                println(Thread.currentThread().name)
                throw RuntimeException("MyError")
            }
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    fun asyncCancel() = launch(CoroutineName("asyncCancel")) {
        println(Thread.currentThread().name)

        try {
            withContext<Unit>(Dispatchers.Default) {
                println(Thread.currentThread().name)
                throw CancellationException("MyCancel")
            }
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    fun asyncDispose(timeMillis: Long) = launch(CoroutineName("asyncDispose")) {
        println(Thread.currentThread().name)

        try {
            withContext(Dispatchers.Default) {
                println(Thread.currentThread().name)
                delay(timeMillis * 4)
            }
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    @Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")
    fun parallelFail() = launch(CoroutineName("parallelFail")) {
        println(Thread.currentThread().name)

        try {
            coroutineScope {
                val d1: Deferred<*> = async(Dispatchers.Default +
                        CoroutineName("child1ParallelFail")) {
                    try {
                        println(Thread.currentThread().name)
                        delay(4000)
                        throw RuntimeException("TooLateError")
                    } finally {
                        finally1Spy = true
                    }
                }
                val d2: Deferred<*> = async(Dispatchers.Default +
                        CoroutineName("child2ParallelFail")) {
                    try {
                        println(Thread.currentThread().name)
                        delay(1000)
                        throw RuntimeException("MyError")
                    } finally {
                        finally2Spy = true
                    }
                }

                d1.await()
                d2.await()
            }
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    @Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")
    fun parallelCancel() = launch(CoroutineName("parallelCancel")) {
        println(Thread.currentThread().name)

        try {
            coroutineScope {
                val d1 = async(Dispatchers.Default +
                        CoroutineName("child1ParallelCancel")) {
                    try {
                        println(Thread.currentThread().name)
                        delay(4000)
                    } finally {
                        finally1Spy = true
                    }
                }
                val d2 = async(Dispatchers.Default +
                        CoroutineName("child2ParallelCancel")) {
                    try {
                        println(Thread.currentThread().name)
                        delay(1000)
                    } finally {
                        finally2Spy = true
                    }
                }

                throw CancellationException("MyCancel")
                d1.await()
                d2.await()
            }
        } catch (ex: CancellationException) {
            logger.debug("Coroutine cancelled.", ex)
            throw ex
        } catch (ex: Throwable) {
            catchSpy = ex
        } finally {
            finallySpy = true
        }
    }

    fun parallelDispose(timeMillis: Long) =
            launch(CoroutineName("parallelDispose")) {
                println(Thread.currentThread().name)

                try {
                    withContext(Dispatchers.Default) {
                        println(Thread.currentThread().name)

                        coroutineScope {
                            val d1 = async(Dispatchers.Default +
                                    CoroutineName("child1ParallelDispose")) {
                                try {
                                    println(Thread.currentThread().name)
                                    delay(timeMillis * 8)
                                } finally {
                                    finally1Spy = true
                                }
                            }
                            val d2 = async(Dispatchers.Default +
                                    CoroutineName("child2ParallelDispose")) {
                                try {
                                    println(Thread.currentThread().name)
                                    delay(timeMillis * 4)
                                } finally {
                                    finally2Spy = true
                                }
                            }

                            d1.await()
                            d2.await()
                        }
                    }
                } catch (ex: CancellationException) {
                    logger.debug("Coroutine cancelled.", ex)
                    throw ex
                } catch (ex: Throwable) {
                    catchSpy = ex
                } finally {
                    finallySpy = true
                }
            }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
