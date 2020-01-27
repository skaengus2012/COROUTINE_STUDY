package com.nlab.coroutine.chapter4

import kotlinx.coroutines.*

class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    fun onDestroy() {
        cancel()
    }

    fun doSomething() {

        repeat(10) { index ->

            launch {
                delay((index + 1) * 100L)
                println("Coroutine $index is done.")
            }

        }

    }

}