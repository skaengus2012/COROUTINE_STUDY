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

package com.nlab.coroutine.chapter7

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-7-ee309d2f0f7d">Select expression</a>
 */
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val fizz = fizz()
    val buzz = buzz()

    repeat(7) {
        selectFizzBuzz(fizz, buzz)
    }

    coroutineContext.cancelChildren()
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.fizz(): ReceiveChannel<String> = produce {
    while (true) {
        delay(300)
        send("Fizz")
    }
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.buzz(): ReceiveChannel<String> = produce {
    while (true) {
        delay(500)
        send("Buzz")
    }
}

private suspend fun selectFizzBuzz(fizz: ReceiveChannel<String>, buzz: ReceiveChannel<String>) {
    select<Unit> {
        fizz.onReceive { value -> println("fizz -> '$value'") }
        buzz.onReceive { value -> println("buzz -> '$value'") }
    }
}