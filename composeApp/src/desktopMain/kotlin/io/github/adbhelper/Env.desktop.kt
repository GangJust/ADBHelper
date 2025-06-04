package io.github.adbhelper

actual val isDebug: Boolean
    get() = System.getProperty("compose.application.resources.dir").contains("\\tmp\\")