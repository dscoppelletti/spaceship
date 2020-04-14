/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.gms.coroutines

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Adapts a Google API that returns a `Task` to a supended coroutine that
 * handles the callbacks raised by the task.
 *
 * @param  func Google API.
 * @return      Result of the task.
 * @since       1.0.0
 */
public suspend fun <R> suspendTask(
        func: () -> Task<R>
): R = suspendCoroutine { continuation ->
    func().onComplete(continuation)
}

/**
 * Adapts a Google API that returns a `Task` to a supended coroutine that
 * handles the callbacks raised by the task.
 *
 * @param  func Google API.
 * @param  arg  Argument of the Google API.
 * @return      Result of the task.
 * @since       1.0.0
 */
public suspend fun <T, R> suspendTask(
        func: (T) -> Task<R>,
        arg: T
): R = suspendCoroutine { continuation ->
    func(arg).onComplete(continuation)
}

/**
 * Adapts a Google API that returns a `Task` to a supended coroutine that
 * handles the callbacks raised by the task.
 *
 * @param  func Google API.
 * @param  arg1 Argument of the Google API.
 * @param  arg2 Argument of the Google API.
 * @return      Result of the task.
 * @since       1.0.0
 */
public suspend fun <T1, T2, R> suspendTask(
        func: (T1, T2) -> Task<R>,
        arg1: T1,
        arg2: T2
): R = suspendCoroutine { continuation ->
    func(arg1, arg2).onComplete(continuation)
}

/**
 * Adapts the callbacks raised by this task to a suspended couroutine.
 *
 * @receiver              Task.
 * @param    continuation Continuation after a suspension point.
 */
private fun <R> Task<R>.onComplete(continuation: Continuation<R>) {
    this.addOnSuccessListener { result ->
        continuation.resume(result)
    }
    this.addOnCanceledListener {
        continuation.resumeWithException(CancellationException())
    }
    this.addOnFailureListener { ex ->
        continuation.resumeWithException(ex)
    }
}
