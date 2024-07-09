package group.ost.mailer.server.request

import group.ost.mailer.server.vo.SendingReason
import kotlinx.serialization.Serializable

@Serializable
data class SendEmailRequest(
    val receiverEmailAddress: String,
    val subject: String,
    val plainText: String,
    val reason: SendingReason
)