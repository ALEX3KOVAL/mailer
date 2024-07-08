package group.ost.mailer.server.response

import kotlinx.serialization.Serializable

@Serializable
class SendEmailMessageResponse(
    val email: String,
    val error: Error? = null
) {
    @Serializable
    enum class Error {
        EMAIL_SENDING_FAILED
    }
}