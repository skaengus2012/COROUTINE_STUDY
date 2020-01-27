package com.nlab.coroutine.test

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.io.IOException
import java.lang.Exception

/**
 * @see <a href="https://tourspace.tistory.com/154?category=797357">Exception</a>
 */
class Chapter5Test {

    /**
     * Exception 의 파장
     */
    @Test fun `test propagation of exception`() = runBlocking<Unit> {

        // 예외를 외부로 전파
        GlobalScope.launch {
            println("Throwing exception from launch ${Thread.currentThread()}")
            throw Exception()
        }.join()

        println("joined job failed")

        // 예외를 노출
       val deferred: Deferred<Int> = GlobalScope.async {
            println("Throwing exception from async ${Thread.currentThread()}")
            throw Exception()
        }

        delay(1000L)

        try {
            deferred.await()
        } catch (e: Exception) {
            println("catch await [$e]")
        }

    }

    @Test fun `catch exception using coroutineExceptionHandler`() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("caught in DefaultExceptionHandler $coroutineContext $throwable")
        }

        runBlocking {

            // 오직 이 방식은 GlobalScope 만 가능한 것으로 보임.
            // runBlocking 내부의 launch 에선 작동안함..
            val job = GlobalScope.launch(coroutineExceptionHandler) {
                throw Exception()
            }

            val deferred = GlobalScope.async(Dispatchers.Unconfined + coroutineExceptionHandler) {
                throw Exception()
            }

            joinAll(job, deferred)
        }
    }

    @Test fun `cancel child coroutine`() = runBlocking {
        launch {

            val child = launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    println("Child is cancelled")
                }
            }

            yield()
            println("Cancelling child")
            child.cancelAndJoin()
            yield()
            println("Parent is not cancelled")

        }.join()
    }

    @Test fun `exception in child coroutine`() = runBlocking {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("caught in DefaultExceptionHandler $coroutineContext $throwable")
        }

        val job = GlobalScope.launch(coroutineExceptionHandler) {

            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    println("Child is cancelled")
                }
            }

            launch {
                delay(10)
                println("Second child throws an exception")
                throw Exception()
            }
        }

        job.join()
        println("End runBlocking")
    }

    @Test fun `throw first exception if exceptions throw`() = runBlocking {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("caught in DefaultExceptionHandler $coroutineContext $throwable")
        }

        GlobalScope.launch(coroutineExceptionHandler) {

            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    throw ArithmeticException()
                }
            }

            launch {
                // 여러 종류의 exception 이 발생할 시, 오직 첫번째만 exception 이 throw 됨.
                delay(100)
                throw IOException()
            }

            delay(Long.MAX_VALUE)

        }.join()

    }

}