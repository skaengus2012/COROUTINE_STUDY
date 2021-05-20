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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-3-be7e46031fd3">Channel</a>
 */
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    var numbers = numbersFrom(2)
    repeat(10) {
        val number = numbers.receive().also { println(it) }
        numbers = filter(numbers) { x -> x % number != 0 }
    }

    coroutineContext.cancelChildren()
    println("Done")
}

@ExperimentalCoroutinesApi
private fun CoroutineScope.numbersFrom(start: Int): ReceiveChannel<Int> = produce {
    var x = start
    while (true) {
        send(x++)
    }
}

@ExperimentalCoroutinesApi
private fun <T> CoroutineScope.filter(
    receiveChannel: ReceiveChannel<T>,
    predicate: (T) -> Boolean
): ReceiveChannel<T> = produce {
    receiveChannel.consumeEach { x -> if (predicate(x)) send(x) }
}

