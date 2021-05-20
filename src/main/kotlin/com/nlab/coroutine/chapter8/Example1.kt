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

package com.nlab.coroutine.chapter8

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-8-1b434772a100">Shared mutable state and concurrency</a>
 */
@ObsoleteCoroutinesApi
fun main() = runBlocking<Unit> {
    val actor: SendChannel<CountingMsg> = countActor(Dispatchers.IO)
    massiveRun { actor.send(IncCounter) }
    
    val response = CompletableDeferred<Int>()
    actor.send(GetCounter(response))
    
    println("Counter = ${response.await()}")
    actor.close()
}

private suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100
    val k = 1000

    val times = measureTimeMillis { coroutineScope { List(n) { launch { repeat(k) { action() } } }.joinAll() } }
    println("Completed ${n * k} actions in $times ms")
}

sealed class CountingMsg
object IncCounter : CountingMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CountingMsg()

@ObsoleteCoroutinesApi
fun CoroutineScope.countActor(
    context: CoroutineContext
): SendChannel<CountingMsg> = actor(context) { 
    var counter = 0 
    channel.consumeEach { msg ->
        when(msg) {
            is IncCounter -> ++counter
            is GetCounter -> msg.response.complete(counter)
        }
    }
}
