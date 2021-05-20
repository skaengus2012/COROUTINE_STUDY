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

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking


/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-9-a-d0082d9f3b89">Asynchronous Flow</a>
 */
fun main() = runBlocking {
    val numberFlow: Flow<Int> = flow {
        (1..11).forEach { i ->
            delay(100)
            emit(i)
        }
    }

    numberFlow
        .transform { request ->
            emit("Number $request")
            emit("Number: $request")
        }
        .collectLatest { println(it) }
}