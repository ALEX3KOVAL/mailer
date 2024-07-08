package group.ost.mailer.server.di

import group.ost.mailer.domain.service.MailerService
import group.ost.mailer.server.App
import group.ost.mailer.server.configure.Configuration
import group.ost.mailer.server.database.AppDatabase
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.*

fun appModule(app: App) = module {
    single<KLogger> { params ->
        KotlinLogging.logger(params.get<String>(0))
    }
    single<Configuration> { Configuration.read() }
    single<App> { app }
    single<MailerService>(createdAtStart = true) {
        val folderName = "mailer"
        val emailingAuthProperties = this::class.java
            .getResourceAsStream("/$folderName/emailing.auth.yaml")!!
            .bufferedReader(Charsets.UTF_8)
            .use { reader -> reader.readText() }

        this::class.java
            .getResourceAsStream("/$folderName/emailing.common.properties")!!
            .bufferedReader(Charsets.UTF_8)
            .let { reader -> Properties().apply { load(reader) } }
            .let { properties ->
                MailerService(
                    emailingAuthProperties = emailingAuthProperties,
                    properties = properties
                )
            }
    }
    singleOf(::AppDatabase) { createdAtStart() }
}
