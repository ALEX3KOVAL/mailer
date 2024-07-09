package group.ost.mailer.server.response

import kotlinx.serialization.Serializable

@Serializable
class SendEmailResponse(
    val email: String,
    val error: Error? = null
) {
    @Serializable
    enum class Error {
        SENDING_FAILED,
        EMAIL_ALREADY_EXISTS,
        EMAIL_NOT_FOUND
    }
}