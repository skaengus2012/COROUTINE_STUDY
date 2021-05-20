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

package com.nlab.coroutine.chapter3

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-3-be7e46031fd3">Channel</a>
 */
fun main() = runBlocking {
    val channel = Channel<Int>()

    launch {
        for (x in 10..15) {
            delay(500)
            channel.send(x)
        }
        channel.close()
    }

    launch {
        for (x in channel) { println(x) }
    }

    println("Done")
}