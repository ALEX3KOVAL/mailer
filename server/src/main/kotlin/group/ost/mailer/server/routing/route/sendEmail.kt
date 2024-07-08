package group.ost.mailer.server.routing.route

import group.ost.mailer.domain.service.MailerService
import group.ost.mailer.server.request.SendEmailRequest
import group.ost.mailer.server.extension.getAPIConfiguration
import group.ost.mailer.server.response.SendEmailMessageResponse
import group.ost.mailer.server.extension.sendingDto
import group.ost.mailer.server.util.getFileLogger
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.sendEmail() = authenticate {
    val apiVersion = getAPIConfiguration().version

    post<SendEmailRequest>("/$apiVersion/mailer/sendEmail") { request ->
        val service by inject<MailerService>()
        val dto = request.sendingDto

        runCatching {
            service
                .sendEmailAsync(dto)
                .getOrThrow()
        }
            .onSuccess {
                return@post call.respond(
                    message = SendEmailMessageResponse(
                        email = request.receiverEmailAddress
                    )
                )
            }
            .onFailure { exc ->
                getFileLogger().value
                    .error {
                        (
                            (exc.message ?: exc.cause?.message)
                            ?.let { "" }
                            ?: "Неизвестная ошибка при попытке отправить письмо на почтовый адрес ${dto.toAddress}\n"
                        ) + exc.stackTraceToString()
                    }

                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = SendEmailMessageResponse(
                        email = request.receiverEmailAddress,
                        error = SendEmailMessageResponse.Error.EMAIL_SENDING_FAILED
                    )
                )
            }
    }
}