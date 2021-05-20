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

package com.nlab.coroutine.chapter9

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * @see <a href="https://myungpyo.medium.com/stateflow-와-sharedflow-32fdb49f9a32">StateFlow & SharedFlow</a>
 */
fun main() = runBlocking {
    // similar with BehaviorSubject
    val stateFlow = MutableStateFlow("State#1").apply {
        launch { 
            delay(100)
            emit("State#2")
        }
    }
    coroutineScope {
        printTake1ValueFrom(stateFlow)
        delay(200)
        printTake1ValueFrom(stateFlow)
    }
    
    // similar with PublishSubject
    val sharedFlow = MutableSharedFlow<String>().apply {
        launch {
            emit("Shared#1")
            delay(500)
            emit("Shared#1")
        }
    }
    supervisorScope {
        // cannot receive any value.
        launch {
            withTimeout(100) {
                printTake1ValueFrom(sharedFlow)
            }
        }
    }
    withTimeout(500) {
        printTake1ValueFrom(sharedFlow)
    }
}

private suspend fun printTake1ValueFrom(flow: Flow<*>) {
    flow.take(1).collect { println(it) }
}
