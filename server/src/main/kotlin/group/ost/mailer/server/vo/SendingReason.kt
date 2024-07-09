package group.ost.mailer.server.vo

import kotlinx.serialization.Serializable

@Serializable
enum class SendingReason {
    registration,
    restorePassword,
    change
}