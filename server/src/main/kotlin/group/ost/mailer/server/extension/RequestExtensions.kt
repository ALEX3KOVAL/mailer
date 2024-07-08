package group.ost.mailer.server.extension

import group.ost.mailer.domain.dto.SendEmailDTO
import group.ost.mailer.domain.vo.EmailAddress
import group.ost.mailer.domain.vo.PlainText
import group.ost.mailer.domain.vo.Subject
import group.ost.mailer.server.request.SendEmailRequest

internal val SendEmailRequest.sendingDto
    get() = SendEmailDTO(
        toAddress = EmailAddress(receiverEmailAddress).getOrThrow(),
        subject = Subject(subject).getOrThrow(),
        plainText = PlainText(plainText)
    )