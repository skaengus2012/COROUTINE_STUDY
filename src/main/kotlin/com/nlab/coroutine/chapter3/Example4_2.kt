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

/**
 * @see <a href="https://myungpyo.medium.com/코루틴-공식-가이드-자세히-읽기-part-3-be7e46031fd3">Channel</a>
 */
fun main() {
    var numbers = getNumbersFrom()
    (1..10).forEach { i ->
        val number = numbers.next().also { println(it) }
        numbers = getFilteredNumbers(numbers, number)
    }
    println("Done")
}

private fun getNumbersFrom(): Iterator<Int> = iterator {
    var num = 2
    while (true) yield(num++)
}

private fun getFilteredNumbers(start: Iterator<Int>, number: Int): Iterator<Int> = iterator {
    start.forEach { i -> if (i % number != 0) yield(i) }
}