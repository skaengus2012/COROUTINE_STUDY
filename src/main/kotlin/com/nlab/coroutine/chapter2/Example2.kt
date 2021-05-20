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

package com.nlab.coroutine.chapter2

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-2-ccd47699b520">Cancellation and Timeout</a>
 */
fun main() = runBlocking {
    try {
        withTimeout(1300L) { SleepingBed().use { it.sleep(10) } }
    } finally {
        println("I'm running finally!")
    }
}