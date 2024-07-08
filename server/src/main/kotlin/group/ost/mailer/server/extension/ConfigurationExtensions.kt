package group.ost.mailer.server.extension

import group.ost.mailer.server.configure.Configuration
import org.koin.mp.KoinPlatform

private val koin = KoinPlatform.getKoin()

fun getJWTConfiguration(): Configuration.JWT = koin
    .inject<Configuration>().value
    .jwt

fun getAPIConfiguration(): Configuration.API = koin
    .inject<Configuration>().value
    .api

