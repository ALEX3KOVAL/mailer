package group.ost.mailer.server.routing.route

import group.ost.mailer.domain.service.MailerService
import group.ost.mailer.server.database.table.UserEmailTable
import group.ost.mailer.server.request.SendEmailRequest
import group.ost.mailer.server.extension.getAPIConfiguration
import group.ost.mailer.server.response.SendEmailResponse
import group.ost.mailer.server.extension.sendingDto
import group.ost.mailer.server.util.getFileLogger
import group.ost.mailer.server.vo.SendingReason
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.sendEmail() {
    val apiVersion = getAPIConfiguration().version

    post<SendEmailRequest>("/$apiVersion/mailer/sendEmail") { request ->
        val service by inject<MailerService>()
        val dto = request.sendingDto

        val row = UserEmailTable.getByEmail(request.receiverEmailAddress)

        when (request.reason) {
            SendingReason.registration,
            SendingReason.change -> {
                if (row != null) {
                    return@post call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = SendEmailResponse(
                            email = request.receiverEmailAddress,
                            error = SendEmailResponse.Error.EMAIL_ALREADY_EXISTS

                        )
                    )
                }
            }
            SendingReason.restorePassword -> {
                if (row == null) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        SendEmailResponse(
                            email = request.receiverEmailAddress,
                            error = SendEmailResponse.Error.EMAIL_NOT_FOUND
                        )
                    )
                }
            }
        }

        runCatching {
            service
                .sendEmailAsync(dto)
                .getOrThrow()
        }
            .onSuccess {
                return@post call.respond(
                    message = SendEmailResponse(
                        email = request.receiverEmailAddress
                    )
                )
            }
            .onFailure { exc ->
                getFileLogger().value
                    .error {
                        (
                            (exc.message ?: exc.cause?.message)
                                ?: "Неизвестная ошибка при попытке отправить письмо на почтовый адрес ${dto.toAddress}\n"
                            ) + "\n" + exc.stackTraceToString()
                    }

                return@post call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = SendEmailResponse(
                        email = request.receiverEmailAddress,
                        error = SendEmailResponse.Error.SENDING_FAILED
                    )
                )
            }
    }
}