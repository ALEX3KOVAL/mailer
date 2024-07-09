package group.ost.mailer.server.database.table

import group.ost.mailer.server.database.core.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import java.util.*

object UserEmailTable : Table("user_email") {

    private val id = uuid("id").clientDefault { UUID.randomUUID() }
    override val primaryKey = PrimaryKey(id)

    private val email = varchar("email", length = 255).uniqueIndex()

    @Serializable
    data class Row(
        val id: String,
        val email: String
    )

    suspend fun getByEmail(email: String) = dbQuery {
        select(UserEmailTable.columns).where {
            UserEmailTable.email eq email
        }.map { it.toRow() }.firstOrNull()
    }

    private fun ResultRow.toRow() = Row(
        id = this[id].toString(),
        email = this[email]
    )
}