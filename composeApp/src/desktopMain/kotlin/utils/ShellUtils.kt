package utils

import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun interface ShellDestroy {
    fun onDestroy()
}

object ShellUtils {

    /**
     * 执行shell命令
     * Execute she'll command
     *
     * @param command command
     * @param directory directory
     */
    fun exec(
        command: Array<String>,
        environment: Array<String>? = null,
        directory: File? = null,
    ): Process {
        val osCommand = when {
            OsUtils.isMac || OsUtils.isLinux -> arrayOf("sh", "-c", *command)
            OsUtils.isWindows -> arrayOf("cmd", "/c", *command)
            else -> throw UnsupportedOperationException("Unsupported operating system")
        }
        return Runtime.getRuntime().exec(osCommand, environment, directory)
    }

    /**
     * 执行shell命令
     * Execute she'll command
     *
     * @param command command
     * @param directory directory
     * @param onInput onInput
     * @param onOutput onOutput
     * @param onError onError
     */
    fun exec(
        command: Array<String>,
        environment: Array<String>? = null,
        directory: File? = null,
        onInput: (InputStream, ShellDestroy) -> Unit,
        onOutput: (OutputStream, ShellDestroy) -> Unit,
        onError: (InputStream, ShellDestroy) -> Unit,
    ): ShellDestroy {
        val process = exec(command, environment, directory)
        val onDestroy = ShellDestroy { process.destroy() }
        process.inputStream.use { onInput(it, onDestroy) }
        process.outputStream.use { onOutput(it, onDestroy) }
        process.errorStream.use { onError(it, onDestroy) }
        process.destroyForcibly()
        return onDestroy
    }


    /**
     * 设置环境变量
     * Set environment variables
     *
     * @param name name
     * @param values value
     */
    fun environment(
        name: String,
        vararg values: String,
    ): Array<String> {
        val symbol = when {
            OsUtils.isMac || OsUtils.isLinux -> ":"
            OsUtils.isWindows -> ";"
            else -> throw UnsupportedOperationException("Unsupported operating system")
        }

        return System.getenv()
            .map {
                if (it.key.lowercase() == name.lowercase()) "${it.key}=${it.value}$symbol${values.joinToString(symbol)}"
                else "${it.key}=${it.value}"
            }
            .toTypedArray()
    }
}