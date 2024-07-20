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
        command: String,
        directory: File? = null,
    ): Process {
        val array = command.split(" ").toTypedArray()
        return build(directory, *array).start()
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
        command: String,
        directory: File? = null,
        onInput: (InputStream, ShellDestroy) -> Unit,
        onOutput: (OutputStream, ShellDestroy) -> Unit,
        onError: (InputStream, ShellDestroy) -> Unit,
    ): ShellDestroy {
        val array = command.split(" ").toTypedArray()
        val process = build(directory, *array).start()
        val onDestroy = ShellDestroy { process.destroy() }
        process.inputStream.use { onInput(it, onDestroy) }
        process.outputStream.use { onOutput(it, onDestroy) }
        process.errorStream.use { onError(it, onDestroy) }
        process.destroyForcibly()
        return onDestroy
    }

    /**
     * 构建ProcessBuilder
     * Build ProcessBuilder
     *
     * @param directory directory
     * @param commands commands
     */
    private fun build(
        directory: File?,
        vararg commands: String,
    ): ProcessBuilder {
        return ProcessBuilder(*commands)
            .directory(directory)
    }
}