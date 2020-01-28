package com.nlab.coroutine.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import org.junit.jupiter.api.Test

/**
 * BlockingQueue 와 비슷한 느낌이라고 함
 *
 * <a href="https://tourspace.tistory.com/156?category=797357">Channel</a>
 */
class Chapter7Test {

    @Test
    fun `sending iterate number using channel and subscribe`() = runBlocking {
        // 버퍼를 줄 수 있음.
        val channel = Channel<Int>(3)
        launch(Dispatchers.Default) {
            (1..5).forEach {
                delay(it * 100L)
                println("sending number [$it]")
                channel.send(it * it)
            }

            // 채널 취소 처리
            channel.close()
        }

        delay(1000)
        for (number in channel) {
            // 받지 않으면, 던질 수 없음
            println("receive number [$number]")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `pipeline channel`() = runBlocking {
        val numbers = produce(Dispatchers.Default) { (1..5).forEach { send(it) } }
        val filteredEven = produce(Dispatchers.Default) {
            for (n in numbers) {
                if (n % 2 == 0) {
                    send(n)
                }
            }
        }

        for (n in filteredEven) {
            println(n)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test fan-out`() = runBlocking {
        val infiniteNumbers = Channel<Int>().apply {
            sendNumber(
                channel = Channel(),
                delay = 500
            )

        }

        repeat(5) {
            launch(Dispatchers.Default) {
                // consumeEach 사용시, 특정 coroutine 에서 예외가 발생하면 모든 coroutine 이 종료된다.
                for (n in infiniteNumbers) {
                    println("print [$n] in $it coroutine")
                }
            }
        }

        delay(10000L)
        coroutineContext.cancelChildren()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test fan-in`() = runBlocking {
        val channel = Channel<Int>(3)

        launch { sendNumber(channel, 200) }
        launch { sendNumber(channel, 500) }

        repeat(6) { println(channel.receive()) }

        coroutineContext.cancelChildren()
    }

    private suspend fun sendNumber(channel: Channel<Int>, delay: Long) {
        var n = 1
        while (true) {
            delay(delay)
            channel.send(n++)
        }
    }
}
