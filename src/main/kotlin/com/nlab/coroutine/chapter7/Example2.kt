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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-7-ee309d2f0f7d">Select expression</a>
 */
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val a: ReceiveChannel<String> = produce {
        repeat(4) { send("Hello $it") }
    }

    val b: ReceiveChannel<String> = produce {
        repeat(4) { send("World $it") }
    }

    repeat(8) {
        println(selectAorB(a, b))
    }

    coroutineContext.cancelChildren()
}

private suspend fun selectAorB(a: ReceiveChannel<String>, b: ReceiveChannel<String>): String {
    return select {
        a.onReceiveCatching { result ->
            result.getOrNull()?.let { "a -> '${it}'" } ?: "Channel 'a' is closed"
        }

        b.onReceiveCatching { result ->
            result.getOrNull()?.let { "b -> '${it}'" } ?: "Channel 'b' is closed"
        }
    }
}
