/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nlab.coroutine.test

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Chapter2Test {

    @Test fun `cancel job when 1300 ms after coroutine start`() = runCancelAndJoinLaunchTemplate {
        launch(Dispatchers.Default) {
            repeat(
                times = 1000,
                action = {
                    println("invoke action time [$it]")
                    delay(500)
                }
            )
        }
    }

    @Test fun `cancel infinite loop job using yield when 1300 ms after coroutine start`() = runCancelAndJoinLaunchTemplate {
        var nextPrintTime = System.currentTimeMillis()
        launch(Dispatchers.Default) {
            var i = 0
            while (true) {
                yield() // launch block 양보

                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
    }

    @Test fun `cancel infinite loop job using active flag when 1300 ms after coroutine start`() = runCancelAndJoinLaunchTemplate {
        var nextPrintTime = System.currentTimeMillis()
        launch(Dispatchers.Default) {
            var i = 0
            while (isActive) { // active flag 체크
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
    }

    /**
     * try-with-resource 에서의 finally
     */
    @Test fun `closing resource with finally when canceled coroutine`() = runCancelAndJoinLaunchTemplate {
        launch(Dispatchers.Default) {
            try {
                repeat(
                    times = 1000,
                    action = {
                        println("invoke action time [$it]")
                        delay(500)
                    }
                )
            } finally {
                // use 에서도 사용 가능
                println("I'm running finally")
            }
        }
    }

    @Test fun `none cancellable block test in finally block`() = runBlocking {
        // TODO 왜 inline 으로 넘겼을 시, 안되는지 확인 필요.
        // https://github.com/Kotlin/kotlinx.coroutines/issues/1175
        val job = launch(Dispatchers.Default) {
            try {
                repeat(
                    times = 1000,
                    action = {
                        println("invoke action time [$it]")
                        delay(500)
                    }
                )
            } finally {
                withContext(NonCancellable) {
                    println("I'm running finally")
                    delay(1000L)
                    println("And I've delayed for 1 ms because I'm non-cancellable")
                }
            }
        }

        delay(1300)
        println("main: Now I'm tried of waiting")
        job.cancelAndJoin()
        println("main: Now I can quit")
    }

    private inline fun runCancelAndJoinLaunchTemplate(
        crossinline launchProvider: suspend CoroutineScope.() -> Job
    ) = runBlocking {
        val job = launchProvider.invoke(this)

        delay(1300)
        println("main: Now I'm tried of waiting")
        job.cancelAndJoin()
        println("main: Now I can quit")
    }

    @Test fun `timeout when 1300 ms after coroutine start`() = runBlocking {
        try {
            withTimeout(1300L) {
                repeat(
                    times = 1000,
                    action = {
                        println("invoke action time [$it]")
                        delay(500)
                    }
                )
            }
        } catch (e: TimeoutCancellationException) {
            println("manage error [${e::class.java.simpleName}]")
        }
    }

    @Test fun `timeoutOrNull when 1300 ms after coroutine start`() = runBlocking {
        assertNull(withTimeoutOrNull(1300L) {
            repeat(
                times = 1000,
                action = {
                    println("invoke action time [$it]")
                    delay(500)
                }
            )
        })
    }
}