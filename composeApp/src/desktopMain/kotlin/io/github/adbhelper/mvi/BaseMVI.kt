package io.github.adbhelper.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun interface MsgCallback {
    fun onMsg(msg: String)
}

fun interface MsgResult<T> {
    fun onResult(msg: String, result: T)
}

abstract class BaseAction

abstract class BaseMVI<A : BaseAction>() : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("err msg: ${throwable.stackTraceToString()}")
    }

    private var singleJobs = mutableMapOf<String, Job?>()

    override fun onCleared() {
        singleJobs.onEach {
            it.value?.cancel()
        }.clear()
    }

    private fun launch(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return viewModelScope.launch(
            context = context + exceptionHandler,
            block = block,
        )
    }

    //
    protected fun launchIO(
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return launch(
            context = Dispatchers.IO,
            block = block,
        )
    }

    protected fun singleLaunchIO(
        key: String,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        singleJobs[key]?.cancel(CancellationException("`$key` single launch IO cancel err!"))
        singleJobs[key] = launch(
            context = Dispatchers.IO + CoroutineName(key),
            block = block,
        )
    }

    protected fun launchMain(
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return launch(
            context = Dispatchers.Main,
            block = block,
        )
    }

    protected fun singleLaunchMain(
        key: String,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        singleJobs[key]?.cancel(CancellationException("`$key` single launch Main cancel err!"))
        singleJobs[key] = launch(
            context = Dispatchers.Main + CoroutineName(key),
            block = block,
        )
    }

    // 分发事件
    abstract fun dispatch(action: A)
}