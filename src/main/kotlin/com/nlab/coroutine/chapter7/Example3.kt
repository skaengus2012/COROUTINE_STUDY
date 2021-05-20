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
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-7-ee309d2f0f7d">Select expression</a>
 */
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val side = Channel<Int>()

    launch {
        side.consumeEach { println("Side channel has $it") }
    }

    produceNumber(side).consumeEach {
        println("Consuming $it")
        delay(250)
    }

    println("Done consuming")
    coroutineContext.cancelChildren()
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.produceNumber(side: SendChannel<Int>): ReceiveChannel<Int> = produce {
    (1..10).forEach { i ->
        delay(100)
        select {
            onSend(i) {}
            side.onSend(i) {}
        }
    }
}