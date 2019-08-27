package it.scoppelletti.spaceship.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CoroutineTest {
    private lateinit var dispatcher: ExecutorCoroutineDispatcher
    private lateinit var spy: CoroutineSpy

    @BeforeTest
    fun setUp() {
        dispatcher = Executors.newSingleThreadExecutor()
                .asCoroutineDispatcher()
        spy = CoroutineSpy(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        spy.dispose()
        dispatcher.close()
    }

    /**
     * Relation (parent job, child job).
     */
    @Test
    fun childJob() = runBlocking {
        val job = spy.childJob(1000)
        assertTrue(spy.job.children.any { it == job }, "ChildJob")
    }

    /**
     * If a child job fails, the child job is finalized and the parent job does
     * not fail and it is not cancelled.
     */
    @Test
    fun sequentialFail() = runBlocking {
        spy.sequentialFail().join()

        assertEquals("MyError", spy.catchSpy?.message, "CatchSpy")
        assertTrue(spy.finallySpy, "Finally")
        assertFalse(spy.job.isCancelled, "Job")
    }

    /**
     * If a child job is cancelled, the child job is finalized and the parent
     * job is not cancelled.
     */
    @Test
    fun sequentialCancel() = runBlocking {
        spy.sequentialCancel().join()

        assertNull(spy.catchSpy)
        assertTrue(spy.finallySpy, "Finally")
        assertFalse(spy.job.isCancelled, "Job")
    }

    /**
     * If an async child job fails, the child job is finalized and the parent
     * job does not fail and it is not cancelled.
     */
    @Test
    fun asyncFail() = runBlocking {
        spy.asyncFail().join()

        assertEquals("MyError", spy.catchSpy?.message, "CatchSpy")
        assertTrue(spy.finallySpy, "Finally")
        assertFalse(spy.job.isCancelled, "Job")
    }

    /**
     * If an async child job is cancelled, the child job is finalized and the
     * parent job is not cancelled.
     */
    @Test
    fun asyncCancel() = runBlocking {
        spy.asyncCancel().join()

        assertNull(spy.catchSpy)
        assertTrue(spy.finallySpy, "Finally")
        assertFalse(spy.job.isCancelled, "Job")
    }

    /**
     * If a job launches an async child job and then the parent job is
     * cancelled, the child job is cancelled and finalized, too.
     */
    @Test
    fun asyncDispose() = runBlocking {
        val job = spy.asyncDispose(1000)

        launch(Dispatchers.Default) {
            delay(1000) // Wait for start
            spy.job.cancel()
            delay(1000) // Wait for finally block
        }.join()

        assertNull(spy.catchSpy)
        assertTrue(spy.finallySpy, "Finally")
        assertTrue(job.isCancelled, "ChildJob")
        assertTrue(spy.job.isCancelled, "Job")
    }

    /**
     * If a job launches two async child jobs and then one of them fails, both
     * child jobs are finalized and the parent job does not fail and it is not
     * cancelled.
     */
    @Test
    fun parallelFail() = runBlocking {
        spy.parallelFail().join()

        assertEquals("MyError", spy.catchSpy?.message, "CatchSpy")
        assertTrue(spy.finallySpy, "Finally")
        assertTrue(spy.finally1Spy, "Finally 1")
        assertTrue(spy.finally2Spy, "Finally 2")
        assertFalse(spy.job.isCancelled, "Job")
    }

    /**
     * If a job launches two async child jobs and then the parent job is
     * cancelled, both child jobs are finalized.
     */
    @Test
    fun parallelCancel() = runBlocking {
        spy.parallelCancel().join()

        assertNull(spy.catchSpy)
        assertTrue(spy.finallySpy, "Finally")
        assertTrue(spy.finally1Spy, "Finally 1")
        assertTrue(spy.finally2Spy, "Finally 2")
        assertFalse(spy.job.isCancelled, "Job")
    }

    /**
     * If a job launches a child job and then the child job launches two async
     * grandchild jobs and then the grandparent job is cancelled, all descendant
     * jobs are cancelled and finalized.
     */
    @Test
    fun parallelDispose() = runBlocking {
        val job = spy.parallelDispose(1000)

        launch(Dispatchers.Default) {
            delay(1000) // Wait for start
            spy.job.cancel()
            delay(1000) // Wait for finally block
        }.join()

        assertNull(spy.catchSpy)
        assertTrue(spy.finallySpy, "Finally")
        assertTrue(spy.finally1Spy, "Finally 1")
        assertTrue(spy.finally2Spy, "Finally 2")
        assertTrue(job.isCancelled, "ChildJob")
        assertTrue(spy.job.isCancelled, "Job")
    }
}

