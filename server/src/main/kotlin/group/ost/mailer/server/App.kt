package group.ost.mailer.server

import group.ost.mailer.server.di.appModule
import io.ktor.server.plugins.contentnegotiation.*
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import group.ost.mailer.server.configure.Configuration
import group.ost.mailer.server.database.table.UserTable
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.context.startKoin
import org.koin.logger.slf4jLogger
import group.ost.mailer.server.routing.restRoutes
import group.ost.mailer.server.security.JwtConfig
import group.ost.mailer.server.vo.UserPrincipal
import group.ost.mailer.server.response.UnauthorizedResponse
import group.ost.mailer.server.security.JwtConfig.userId

interface App {
    companion object {
        fun create(args: Array<String>) : App = AppImpl(args)
    }
    fun start()
    fun stop()
}

private class AppImpl(args: Array<String>) : App {
    private val koinApp = startKoin {
        slf4jLogger()
        modules(appModule(this@AppImpl))
    }

    private val applicationProperties = applicationProperties {
        developmentMode = true
        module {
            koinApp

            install(ContentNegotiation) {
                json(restSerialization)
            }

            install(CORS) {
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Post)
                allowHeader(HttpHeaders.Authorization)
                allowCredentials = true
                anyHost()
            }

            authentication {
                jwt {
                    verifier(JwtConfig.verifier)
                    challenge { _, _ ->
                        suspend fun unauthorized(error: UnauthorizedResponse.Error) =
                            call.respond(HttpStatusCode.Unauthorized, UnauthorizedResponse(error = error))

                        val header = call.request.headers[HttpHeaders.Authorization]
                            ?: return@challenge unauthorized(UnauthorizedResponse.Error.ACCESS_TOKEN_MISSING)

                        if (header.isEmpty())
                            return@challenge unauthorized(UnauthorizedResponse.Error.ACCESS_TOKEN_EMPTY)

                        try {
                            val token = header.replace("Bearer ", "")
                            JwtConfig.verifier.verify(token)
                        } catch (e: Throwable) {
                            unauthorized(if (e is JWTVerificationException) {
                                when (e) {
                                    is JWTDecodeException -> UnauthorizedResponse.Error.ACCESS_TOKEN_INVALID
                                    else -> if (e.localizedMessage.contains("expired"))
                                        UnauthorizedResponse.Error.ACCESS_TOKEN_EXPIRED
                                    else
                                        UnauthorizedResponse.Error.ACCESS_TOKEN_INVALID
                                }
                            } else
                                UnauthorizedResponse.Error.ACCESS_TOKEN_UNKNOWN_ERROR)
                        }
                    }
                    validate { credential ->
                        val user = UserTable.getById(credential.payload.userId)
                        if (user != null) UserPrincipal(user.id) else null
                    }
                }
            }

            routing {
                restRoutes()
            }
        }
    }

    private val server = embeddedServer(
        CIO,
        applicationProperties,
        configure = {
            val configuration = koinApp.koin
                .get<Configuration>()
                .ktor

            connector {
                host = configuration.host
                port = configuration.port
            }
        }
    )

    override fun start() {
        server.start(wait = true)
    }

    override fun stop() {
        server.stop(1000L, 5000L)
    }
}