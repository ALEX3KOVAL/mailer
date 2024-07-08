package group.ost.mailer.server.database.table

import group.ost.mailer.server.database.core.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import java.util.*

object UserTable : Table("user") {

    private val id = uuid("id").clientDefault { UUID.randomUUID() }
    private val enabled = bool("enabled").default(true)
    private val deleted = bool("deleted").default(false)
    private val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }

    override val primaryKey = PrimaryKey(id)

    @Serializable
    data class Row(
        val id: String,
        val enabled: Boolean,
        val deleted: Boolean,
        val createdAt: Long
    )

    suspend fun getById(id : String) = dbQuery {
        select(UserTable.columns).where {
            UserTable.id eq UUID.fromString(id)
        }.map { it.toRow() }.firstOrNull()
    }

    private fun ResultRow.toRow() = Row(
        id = this[id].toString(),
        enabled = this[enabled],
        deleted = this[deleted],
        createdAt = this[createdAt]
    )
}