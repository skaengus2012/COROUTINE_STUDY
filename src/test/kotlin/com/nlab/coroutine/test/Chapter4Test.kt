package com.nlab.coroutine.test

import com.nlab.coroutine.chapter4.Activity
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @see <a href="https://tourspace.tistory.com/153?category=797357">context 와 dispatcher</a>
 */
class Chapter4Test {

    @Test fun `print thread name per dispatchers`() = runBlocking<Unit> {

        launch {
            println("main runBlocking       : I'm working in thread ${Thread.currentThread().name}")
        }

        launch(Dispatchers.Unconfined) {
            println("unconfined       : I'm working in thread ${Thread.currentThread().name}")
        }

        launch(Dispatchers.Default) {
            println("default       : I'm working in thread ${Thread.currentThread().name}")
        }

        @UseExperimental(ObsoleteCoroutinesApi::class)
        launch(newSingleThreadContext("MyOwnThread")) {
            println("newSingleThreadContext       : I'm working in thread ${Thread.currentThread().name}")
        }

    }

    @Test fun `print thread name using unconfined`() = runBlocking {

        val jobUnConfined = launch(Dispatchers.Unconfined) {
            // unconfined 는 처음 실행된 Caller 의 Thread 에서 동작
            println("Unconfined    : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            // suspending 후에는 적절한 Thread 에 할당되어 동작
            println("Unconfined    : I'm working in thread ${Thread.currentThread().name}")
        }

        val jobMainBlocking = launch {
            println("main runBlocking    : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("main runBlocking    : I'm working in thread ${Thread.currentThread().name}")
        }

        jobUnConfined.join()
        jobMainBlocking.join()

    }

    /**
     * -Dkotlinx.coroutines.debug 옵션을 추가하면 Coroutine 에 대한 추가정보도 볼 수 있다고 하지만, 안해도 잘보이는
     */
    @Test fun `log test for debugging coroutine`() = runBlocking {
        val a = async {
            delay(1000)
            log("I'm computing a piece of answer")

            10
        }

        val b = async {
            delay(2000)
            log("I'm computing another piece of answer")

            40
        }

        log("The answer is ${a.await() + b.await()}")
    }

    private fun log(message: String) = println("[${Thread.currentThread().name}] $message")

    @Test
    @UseExperimental(ObsoleteCoroutinesApi::class)
    fun `jumping between threads`() {

        newSingleThreadContext("Ctx 1").use { ctx1 ->

            newSingleThreadContext("Ctx 2").use {  ctx2 ->

                runBlocking(ctx1) {
                    log("Started in ctx1")

                    withContext(ctx2) {
                        log("Started in ctx2")
                    }

                    log("Back to ctx1")
                }

            }

        }

    }

    @Test fun `job in the context`() = runBlocking {
        // job 꺼내오기
        println("My job is ${coroutineContext[Job]}")

        assertEquals(isActive, coroutineContext[Job]?.isActive ?: false)
    }

    @Test fun `cancel parent job that have a GlobalScope job when delayed 500ms`() = runBlocking {

        val request = launch {
            // 코루틴을 launch 시키면, 해당 코루틴의 CoroutineScope.coroutineContext 를 상속받음
            // 이 때, 생성되는 job 역시 부모 job 의 자식이 됨.

            // 부모 Coroutine 이 취소되면, 자식 Coroutine 도 취소됨

            GlobalScope.launch {
                // GlobalScope 는 request 의 자식이 아님.

                println("job1 : I run in GlobalScope and execute independently")
                delay(1000)
                println("job1 : I'm not affected by cancellation of the request")
            }

            launch {
                // 해당 구문은 자식이기 때문에 취소됨.

                delay(100)
                println("job2 : I'm a child of the request coroutine")
                delay(1000)
                println("job2 : I'll not execute this line if my parent request is cancelled")
            }

        }

        delay(500)
        request.cancel()
        delay(1000)
        println("main : Who has survived request cancellation?")

    }

    @Test fun `cancel parent job that have a Default dispatcher job when delayed 500ms`() = runBlocking {

        val request = launch {
            // 코루틴을 launch 시키면, 해당 코루틴의 CoroutineScope.coroutineContext 를 상속받음
            // 이 때, 생성되는 job 역시 부모 job 의 자식이 됨.

            // 부모 Coroutine 이 취소되면, 자식 Coroutine 도 취소됨

            launch(Dispatchers.Default) {
                // Dispatcher 는 비록 GlobalScope 와 동일한 것을 사용하지만, 자식의 job 이기 때문에 취소됨.

                println("job1 : I run in GlobalScope and execute independently")
                delay(1000)
                println("job1 : I'll not execute this line if my parent request is cancelled")
            }

            launch {
                // 해당 구문은 자식이기 때문에 취소됨.

                delay(100)
                println("job2 : I'm a child of the request coroutine")
                delay(1000)
                println("job2 : I'll not execute this line if my parent request is cancelled")
            }

        }

        delay(500)
        request.cancel()
        delay(1000)
        println("main : Who has survived request cancellation?")

    }

    @Test fun `test for parental Responsibility`() = runBlocking {
        val request = launch {

            repeat(3) { num ->
                launch {
                    delay((num + 1) * 200L)
                    println("Coroutine $num is done")
                }
            }

            println("request: I'm done and I don't explicitly join my children that are still active")

        }

        request.join()
        println("Now processing of the request is complete")

    }

    @Test fun `naming coroutine for debugging`() = runBlocking {
        log("started main coroutine")

        val v1 = async(CoroutineName("v1Coroutine")) {
            delay(500)
            log("Computing v1")
            252
        }

        val v2 = async(CoroutineName("v2Coroutine")) {
            delay(1000)
            log("Computing v2")
            6
        }

        log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
    }

    @Test fun `combining context element`() = runBlocking<Unit> {
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("default       : I'm working in thread ${Thread.currentThread().name}")
        }

        MainScope()
    }

    @Test fun `cancelled via explicit job`() = runBlocking {
        val activity = Activity()
        println("Launched Coroutine")
        activity.doSomething()
        delay(500L)
        println("destroy activity")
        activity.onDestroy()
    }

    @Test fun `test for thread local`() = runBlocking {
        val threadLocal = ThreadLocal<String?>().apply { set("Main") }

        println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")

        val job = launch(Dispatchers.Default + threadLocal.asContextElement("launch")) {
            println("Launch start, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
            yield()
            println("After yield, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
        }

        delay(500)
        job.join()

        println("After-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
    }

    @Test fun `test for change value of thread local`() = runBlocking {
        val threadLocal = ThreadLocal<String?>().apply { set("Main") }

        println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")

        launch(Dispatchers.Unconfined + threadLocal.asContextElement("launch")) {
            println("Launch start, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
            // 값의 변경은 같은 Coroutine 내부에서만 유효.
            threadLocal.set("launch finished")
            println("Launch end, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
        }.join()

        println("After-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")

        launch(Dispatchers.Unconfined + threadLocal.asContextElement("launch")) {
            println("Try change thread local launch start, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
            withContext(EmptyCoroutineContext + threadLocal.asContextElement("launch finished")) {
                // 값을 바꿀수는 없고, withContext 로 밀어넣어줘야함.
                println("After-main, current thread: ${Thread.currentThread()}, thread local value: ${threadLocal.get()}")
            }
        }.join()

    }

}