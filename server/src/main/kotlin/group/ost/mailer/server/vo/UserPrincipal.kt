package group.ost.mailer.server.vo

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserPrincipal(val id: String) : Principal