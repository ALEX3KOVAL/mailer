package group.ost.mailer.server.request

import kotlinx.serialization.Serializable

@Serializable
data class SendEmailRequest(
    val receiverEmailAddress: String,
    val subject: String,
    val plainText: String
)