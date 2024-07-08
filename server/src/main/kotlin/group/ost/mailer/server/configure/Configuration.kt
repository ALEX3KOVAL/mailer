package group.ost.mailer.server.configure

import kotlinx.serialization.Serializable
import io.github.cdimascio.dotenv.dotenv

@Serializable
class Configuration internal constructor(
    val ktor: Ktor,
    val db: Database,
    val jwt: JWT,
    val api: API
) {
    companion object {
        fun read(): Configuration {
            val dotenv = dotenv()

            return Configuration(
                ktor = Ktor(
                    host = dotenv["SERVER_HOST"],
                    port = dotenv["SERVER_PORT"].toInt()
                ),
                db = Database(
                    name = dotenv["POSTGRES_DB"],
                    user = dotenv["POSTGRES_USER"],
                    password = dotenv["POSTGRES_PASSWORD"],
                    host = dotenv["DATABASE_HOST"],
                    port = dotenv["DATABASE_PORT"].toInt()
                ),
                jwt = JWT(
                    secret = dotenv["JWT_SECRET"]
                ),
                api = API(
                    version = dotenv["API_VERSION"]
                )
            )
        }
    }

    @Serializable
    data class Ktor(val host: String, val port: Int)

    @Serializable
    data class JWT(val secret: String)

    @Serializable
    data class API(val version: String)

    @Serializable
    data class Database(
        val name: String,
        val user: String,
        val password: String,
        val host: String,
        val port: Int
    )
}