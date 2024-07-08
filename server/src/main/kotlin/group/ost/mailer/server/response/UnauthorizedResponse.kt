package group.ost.mailer.server.response

import kotlinx.serialization.Serializable

@Serializable
data class UnauthorizedResponse(
    val error: Error,
) {
    enum class Error {
        ACCESS_TOKEN_MISSING,
        ACCESS_TOKEN_EMPTY,
        ACCESS_TOKEN_INVALID,
        ACCESS_TOKEN_EXPIRED,
        ACCESS_TOKEN_UNKNOWN_ERROR
    }
}