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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-7-ee309d2f0f7d">Select expression</a>
 */
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val channel = Channel<Deferred<String>>()

    launch { switchMapDeferreds(channel).consumeEach { println(it) } }

    channel.send(getStringAsync("BEGIN", 100))
    delay(200)
    channel.send(getStringAsync("SLOW", 500))
    delay(100)
    channel.send(getStringAsync("REPLACE", 100))
    delay(500)
    channel.send(getStringAsync("END", 500))
    delay(1000)
    channel.close()
    delay(500)
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.switchMapDeferreds(
    input: ReceiveChannel<Deferred<String>>
): ReceiveChannel<String> = produce {
    var current = input.receive()
    while (isActive) {
        val value = select<Deferred<String>?> {
            input.onReceiveCatching { it.getOrNull() }
            current.onAwait { value ->
                send(value)
                input.receiveCatching().getOrNull()
            }
        }

        if (value == null) {
            println("Channel was closed")
            break
        } else {
            current = value
        }
    }
}

private fun CoroutineScope.getStringAsync(message: String, delayTime: Long): Deferred<String> = async {
    delay(delayTime)
    message
}