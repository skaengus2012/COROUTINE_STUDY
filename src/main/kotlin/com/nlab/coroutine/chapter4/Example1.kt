/*
 * Copyright (C) 2018 The N's lab Open Source Project
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

package com.nlab.coroutine.chapter4

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-4-70b0c0fb492">중단 함수의 합성</a>
 */
fun main() = runBlocking {
    fun doSomethingUsefulOneAsync(): Deferred<Int> = async {
        delay(1000L)
        13
    }

    fun doSomethingUsefulTwoAsync(): Deferred<Int> = async {
        delay(1000L)
        29
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = doSomethingUsefulOneAsync()
        val two = doSomethingUsefulTwoAsync()
        one.await() + two.await()
    }

    suspend fun doWork(): Long = measureTimeMillis {
        println("The answer is ${concurrentSum()}")
    }

    println("Completed in ${doWork()} ms")
}

