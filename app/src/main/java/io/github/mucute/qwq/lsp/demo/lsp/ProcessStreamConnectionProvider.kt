package io.github.mucute.qwq.lsp.demo.lsp

import io.github.mucute.qwq.lsp.demo.application.AppContext
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider
import java.io.InputStream
import java.io.OutputStream

class ProcessStreamConnectionProvider : StreamConnectionProvider {

    private val processBuilder = ProcessBuilder(AppContext.instance.filesDir.resolve("usr/bin").resolve("typescript-language-server").absolutePath, "--stdio")
        .apply {
            val env = environment()
            env.putAll(System.getenv())
            env["PATH"] = "${System.getenv("PATH")}:${AppContext.instance.filesDir.resolve("usr/bin")}"
            env["LD_LIBRARY_PATH"] = "${System.getenv("LD_LIBRARY_PATH")}:${AppContext.instance.filesDir.resolve("usr/lib")}"
            env["OPENSSL_CONF"] = ""
        }
        .redirectErrorStream(true)

    private lateinit var process: Process

    override val inputStream: InputStream
        get() = process.inputStream

    override val outputStream: OutputStream
        get() = process.outputStream

    override fun start() {
        process = processBuilder.start()
    }

    override fun close() {
        process.destroy()
    }

}
