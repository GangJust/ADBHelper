package mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.cast

fun interface MsgCallback {
    fun onMsg(msg: String)
}

fun interface MsgResult<T> {
    fun onResult(msg: String, result: T)
}

abstract class BaseViewModel<A : BaseAction> : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("err msg: ${throwable.stackTraceToString()}")
    }

    private var singleJobs = mutableMapOf<String, Job?>()
    private var singleJobBundles = mutableMapOf<String, Pair<Job, Bundle>?>()

    override fun onCleared() {
        singleJobs.onEach {
            it.value?.cancel()
        }.clear()

        singleJobBundles.onEach {
            it.value?.first?.cancel()
            it.value?.second?.clear()
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

    protected fun singleLaunchIO(
        key: String,
        launched: suspend CoroutineScope.(Bundle) -> Unit,
        canceled: suspend CoroutineScope.(Job, Bundle) -> Unit,
    ) {
        singleJobBundles[key]?.let {
            launch(context = Dispatchers.IO + CoroutineName("${key}_canceled")) {
                canceled.invoke(this, it.first, it.second)
                it.first.cancel(CancellationException("`$key` single launch IO cancel err!"))
                cancel()
            }
        }

        val bundle = Bundle()
        singleJobBundles[key] = Pair(
            first = launch(context = Dispatchers.IO + CoroutineName(key)) {
                launched.invoke(this, bundle)
            },
            second = bundle
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

    protected fun singleLaunchMain(
        key: String,
        launched: suspend CoroutineScope.(Bundle) -> Unit,
        canceled: suspend CoroutineScope.(Job, Bundle) -> Unit,
    ) {
        singleJobBundles[key]?.let {
            launch(context = Dispatchers.Main + CoroutineName("${key}_canceled")) {
                canceled.invoke(this, it.first, it.second)
                it.first.cancel(CancellationException("`$key` single launch IO cancel err!"))
                cancel()
            }
        }

        val bundle = Bundle()
        singleJobBundles[key] = Pair(
            first = launch(context = Dispatchers.Main + CoroutineName(key)) {
                launched.invoke(this, bundle)
            },
            second = bundle
        )
    }

    // 单次协程取消携带数据
    inner class Bundle {
        private val bundle: MutableMap<String, Any?> = mutableMapOf()

        fun putString(key: String, value: String) {
            bundle[key] = value
        }

        fun put(key: String, value: Any?) {
            bundle[key] = value
        }

        fun getString(key: String, defaultValue: String = ""): String {
            val value = bundle[key] ?: return defaultValue

            return "$value"
        }

        fun <T : Any> get(key: String, clazz: KClass<T>): T? {
            val value = bundle[key]
            if (!clazz.isInstance(value))
                return null
            return clazz.cast(value)
        }

        fun clear() {
            bundle.clear()
        }
    }

    // 分发事件
    abstract fun dispatch(action: A)
}