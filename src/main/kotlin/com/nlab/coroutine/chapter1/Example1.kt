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

package com.nlab.coroutine.chapter1

import kotlinx.coroutines.*

/**
 * @see <a href="https://myungpyo.medium.com/reading-coroutine-official-guide-thoroughly-part-1-98f6e792bd5b">Basics</a>
 */
fun main() = runBlocking {
    SuspendPrintln(this)("World!")
    print("Hello ")
}

private class SuspendPrintln(private val coroutineScope: CoroutineScope) : (String) -> Unit {

    override fun invoke(message: String) {
        coroutineScope.launch { doWorld(message) }
    }

    private suspend fun doWorld(message: String) {
        delay(1000L)
        println(message)
    }
}