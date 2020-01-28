package com.nlab.coroutine.test

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

/**
 * <a href="https://tourspace.tistory.com/155?category=797357">supervision</a>
 */
class Chapter6Test {

    @Test fun `second job alive when first job throw exception`() = runBlocking {
        val supervisor = SupervisorJob()

        try {
            with(CoroutineScope(supervisor)) {

                val firstChild = launch(CoroutineExceptionHandler { _, throwable ->  println("caught $throwable")}) {
                    println("First child failing")
                    throw AssertionError()
                }

                val secondChild = launch {
                    firstChild.join()
                    println("First child is canceled: ${firstChild.isCancelled}, but second one is still alive")

                    try {
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("Second child is cancelled because supervisor is cancelled")
                    }

                }

                firstChild.join()
                println("Cancelling supervisor")
                supervisor.cancel()
                secondChild.join()
            }
        } catch (e: CancellationException) {
            println("coroutineScope is cancelled")
        }
    }

    @Test fun `throw exception in the supervisorScope`() = runBlocking {

        try {
            supervisorScope<Unit> {
                launch {
                    try {
                        println("Child is sleeping")
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("Child is cancelled")
                    }
                }

                // Give our child a chance to execute and print using yield
                yield()
                println("Throwing exception from scope")
                throw AssertionError()
            }
        } catch (e: AssertionError) {
            // supervisorScope 의 경우, 부모로 예외를 던질 수 없기 때문에 자식이 처리할 수단을 마련해줘야 함.
            println("Caught assertion error")
        }

    }

    @Test fun `exceptions in supervised coroutines`() = runBlocking {
        val handler = CoroutineExceptionHandler { _, throwable -> println("Caught $throwable") }

        supervisorScope {
            launch(handler) {
                println("Child throws an exception")
                throw AssertionError()
            }

            println("Scope is completing")
        }
        
    }

    @Test fun `exception handling pile coroutine`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, throwable -> println("Caught $throwable") }

        supervisorScope {
            // 중첩된 coroutine 에서는 맨위의 handler 를 달아야 함
            // handler 는 supervisorJob 동등하거나, 상위에 위치해야 작동함
            launch(handler) {
                launch {
                    launch(handler) {
                        throw AssertionError()
                    }
                }
            }

        }

    }

    @Test fun `exception case with supervisorJob`() = runBlocking<Unit> {
        val job = SupervisorJob()
        val handler = CoroutineExceptionHandler { _, throwable -> println("Caught $throwable") }

        // case #1 : handler 가 상위에 존재
        launch(handler) {
            launch {
                launch(job) {
                    throw AssertionError()
                }
            }
        }

        // case #2 : handler 가 동등한 위치에 있는 경우
        launch {
            launch {
                launch(job + handler) {
                    throw AssertionError()
                }
            }
        }

        // case #3 : handler 가 SupervisorJob 밑에 위치하는 경우
        try {
            launch(job) {
                launch {
                    launch(handler) {
                        throw AssertionError()
                    }
                }
            }
        } catch (e: AssertionError) {
            println("try-catch Caught $e")
        }

    }



}