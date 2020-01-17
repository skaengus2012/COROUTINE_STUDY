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

class Chapter1Test {

    @Test fun `launch when delayed for 1 seconds in the GlobalScope Coroutine`() = runBlocking {
        GlobalScope.launch {
            delay(1000L)
            println("Coroutine ${Thread.currentThread()}")
        }

        println("Hello ${Thread.currentThread()}")
        delay(2000L)
    }

    @Test fun `join launched job when delayed for 1 seconds in the GlobalScope Coroutine`() = runBlocking {
        GlobalScope.launch {
                delay(1000L)
                println("Coroutine ${Thread.currentThread()}")
            }
            .run {
                println("Hello ${Thread.currentThread()}")
                join()
            }
    }

    @Test fun `join 10 job when they are delayed for 1 seconds`() {
        runBlocking {
            // 순서가 보장되지 않음. 즉 Rx 의 mergeDelayed 정도로 생각해볼 수 있음.
            List(10) { number ->
                launch(Dispatchers.IO) {
                    val index = number + 1
                    val delayTime = 1000L / index

                    delay(delayTime)
                    println("launched at index [$index] ${Thread.currentThread()}")
                }
            }.joinAll()

            println("End runBlock ${Thread.currentThread()}")
        }

        println("End function ${Thread.currentThread()}")
    }

    @Test fun `create coroutine scope for match order of invoke`() {
        runBlocking {
            launch(Dispatchers.IO) {
                delay(200L)
                println("invoke 2 ${Thread.currentThread()}")
            }

            coroutineScope {
                val job = launch(Dispatchers.IO) {
                    delay(500L)
                    println("invoke 3 ${Thread.currentThread()}")
                }

                println("invoke 1 ${Thread.currentThread()}")
                job.join()
            }
        }

        println("invoke 4 ${Thread.currentThread()}")
    }
}

