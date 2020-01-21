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
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * @see <a href="https://tourspace.tistory.com/152?category=797357">suspending function 의 구성</a>
 */
class Chapter3Test {

    @Test fun `sum value of useful function`() = measureTimePrintBlockingFunction {
        doSomethingUsefulOne() + doSomethingUsefulTwo()
    }

    @Test fun `sum value of useful function using async`() = measureTimePrintBlockingFunction {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }

        one.await() + two.await()
    }

    @Test fun `sum value of useful function using async with lazy`() = measureTimePrintBlockingFunction {
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }

        delay(1000L)
        one.start()

        delay(1000L)
        two.start()

        one.await() + two.await()
    }

    // inline 블록과 코루틴의 상성은 좋지 않음 ㅡㅡ^
    private fun measureTimePrintBlockingFunction(
        valueSupplier: suspend CoroutineScope.() -> Int
    ) = runBlocking {
        measureTimeMillis { println("The answer is ${valueSupplier.invoke(this)}") }
            .run { println("Completed in $this ms") }
    }

    private suspend fun doSomethingUsefulOne(): Int {
        delay(1000L)
        return 13
    }

    private suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L)
        return 29
    }

    private fun doSomethingUsefulOneGlobalScopeAsync() = GlobalScope.async { doSomethingUsefulOne() }

    private fun doSomethingUsefulTwoGlobalScopeAsync() = GlobalScope.async { doSomethingUsefulTwo() }

    @Test fun `sum value of useful function using async globalScope`() {
        measureTimeMillis {
                // 이 방법은 권장되지 않음. 만약 doSomethingUsefulOneGlobalScopeAsync 의 에러 시, 비동기는 유지됨
                val one = doSomethingUsefulOneGlobalScopeAsync()
                val two = doSomethingUsefulTwoGlobalScopeAsync()

                runBlocking {
                    println("The answer is ${one.await() + two.await()}")
                }
            }
            .run { println("Completed in $this ms") }
    }

    private suspend fun concurrentSum(): Int = coroutineScope {
        // 같은 scope 이기 때문에 에러 시, 비동기가 종료됨
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }

        one.await() + two.await()
    }

    @Test fun `sum value of useful function using async coroutineScope`() = measureTimePrintBlockingFunction {
        concurrentSum()
    }

    @Test fun `invoke failed sum`() {
        runBlocking {
            try {
                failedConcurrentSum()
            } catch (e: ArithmeticException) {
                println("error")
            }
        }
    }

    private suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async {
            println("first async ${Calendar.getInstance().timeInMillis}")
            try {
                delay(Long.MAX_VALUE)
                42
            } finally {
                println("Main finally ${Calendar.getInstance().timeInMillis}")
            }
        }

        val two = async {
            println("second async ${Calendar.getInstance().timeInMillis}")
            try {
                delay(Long.MAX_VALUE)
                42
            } catch (e: CancellationException) {
                println("second child was cancelled ${Calendar.getInstance().timeInMillis}")
                42
            }
        }

        val three: Deferred<Int> = async {
            println("three async ${Calendar.getInstance().timeInMillis}")
            throw ArithmeticException()
        }

        one.await() + two.await() + three.await()
    }

}