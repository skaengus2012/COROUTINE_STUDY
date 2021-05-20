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

package com.nlab.coroutine.chapter6

import kotlinx.coroutines.*

/**
 * 자식 코루틴에서 예외가 발생하면, 부모 코루틴도 종료함.
 * ExceptionHandler 에는 모든 코루틴이 종료되어야 전달
 * 두개 이상의 코루틴이 전달될 경우 먼저 발생한 코루틴을 부모로 Tree 구조로 Wrapping 됨.
 *
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-6-dd7796150ff3">Exception Handling</a>
 */
fun main() = runBlocking<Unit>(CoroutineExceptionHandler { _, throwable -> println("Catch $throwable by handler") }) {
    launch {
        try {
            delay(Long.MAX_VALUE)
        } finally {
            withContext(NonCancellable) {
                delay(2500)
                throw IllegalStateException()
            }
        }
    }

    launch { throw AssertionError() }
}