package group.ost.mailer.server.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import group.ost.mailer.server.configure.Configuration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.mp.KoinPlatform

class AppDatabase {
    private val config = KoinPlatform
        .getKoin()
        .get<Configuration>()
        .db

    private fun pool(): HikariDataSource = HikariDataSource(HikariConfig().apply {

        val host = config.host
        val port = config.port
        val name = config.name
        val user = config.user
        val password = config.password

        poolName = "group.ost"
        driverClassName = org.postgresql.Driver::class.java.getName()
        jdbcUrl = "jdbc:postgresql://$host:$port/$name?user=$user&password=$password"
        maximumPoolSize = 50
        idleTimeout = 30000
        maxLifetime = 60000
        minimumIdle = 5
        isAutoCommit = true
        isAllowPoolSuspension = true
        leakDetectionThreshold = 10000
        transactionIsolation = "TRANSACTION_SERIALIZABLE"
        validate()
    })

    private val db = Database.connect(pool())

    init {
        transaction {
            addLogger(StdOutSqlLogger)
        }
    }
}