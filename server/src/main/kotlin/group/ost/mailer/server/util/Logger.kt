package group.ost.mailer.server.util

import io.github.oshai.kotlinlogging.KLogger
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform

private fun getLogger(name: String) = KoinPlatform
    .getKoin()
    .inject<KLogger> { parametersOf(name) }

/**
 * Получить логгер, который пишет в файл
 */
fun getFileLogger() = getLogger("MAILER")