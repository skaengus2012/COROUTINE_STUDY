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

package com.nlab.coroutine.chapter5

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Activity : CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + job

    fun onCreate() {
        println("onCreate")
        job = Job()
    }

    fun onDestroy() {
        println("onDestroy")
        job.cancel()
    }

    fun doSomething() {
        println("Launched coroutines")

        repeat(10) { i ->
            launch {
                delay((i + 1) * 200L)
                println("Coroutine $i is done")
            }
        }
    }

}