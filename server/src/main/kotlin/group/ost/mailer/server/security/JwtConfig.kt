package group.ost.mailer.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import group.ost.mailer.server.extension.getJWTConfiguration
import group.ost.mailer.server.vo.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*

object JwtConfig {
    private val jwtSecret = getJWTConfiguration().secret
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    //private const val ISSUER = "https://${HOST}"
    private const val USER_ID = "userId"

    val verifier: JWTVerifier = JWT.require(algorithm)
        //.withIssuer(ISSUER)
        .build()

    fun generateToken(userId: String, expiresAt: Long): String = JWT.create()
        //.withIssuer(ISSUER)
        .withClaim(USER_ID, userId)
        .withExpiresAt(Date(System.currentTimeMillis() + expiresAt))
        .sign(algorithm)

    val Payload.userId : String get() = getClaim(USER_ID).asString()
}

val ApplicationCall.userPrincipal get() = authentication.principal<UserPrincipal>()!!