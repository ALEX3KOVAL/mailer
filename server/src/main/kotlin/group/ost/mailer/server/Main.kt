package group.ost.mailer.server

import io.github.oshai.kotlinlogging.KotlinLogging

fun main(args: Array<String>) {
    val app = App.create(args)

    Runtime
        .getRuntime()
        .addShutdownHook(
            object : Thread() {
                private val logger = KotlinLogging.logger {}
                override fun run() {
                    logger.info { "App Shutdown" }
                    app.stop()
                    logger.info { "Server shutdown" }
                }
            }
        )

    app.start()
}