package com.ralphdugue.kogent.data.domain.entities

import kotlinx.serialization.Serializable

/**
 * An SQL data source.
 *
 * This class represents a data source that is an SQL database. The data source can be of any type of SQL database,
 * such as MySQL, PostgresSQL, SQLite, or H2.
 * @param identifier The identifier of the data source.
 * @param host The URL of the SQL database.
 * @param databaseName The name of the SQL database.
 * @param username The username to use to connect to the SQL database.
 * @param password The password to use to connect to the SQL database.
 * @param query The query to execute on the SQL database.
 * @author Ralph Dugue
 */
@Serializable
data class SQLDataSource(
    override val identifier: String,
    override val dataSourceType: DataSourceType = DataSourceType.SQL,
    val databaseType: DatabaseType,
    val host: String,
    val databaseName: String,
    val username: String,
    val password: String,
    val query: String,
) : DataSource {
    /**
     * This enum represents the type of the database.
     */
    @Serializable
    enum class DatabaseType {
        MYSQL,
        POSTGRESQL,
        SQLITE,
        H2,
    }
}

class SQLDataSourceBuilder {
    var identifier: String? = null
    var databaseType: SQLDataSource.DatabaseType? = null
    var host: String? = null
    var databaseName: String? = null
    var username: String? = null
    var password: String? = null
    var query: String? = null

    fun build(): SQLDataSource {
        // Check for required parameters
        val id = identifier ?: throw IllegalArgumentException("Identifier must be provided")
        val type = databaseType ?: throw IllegalArgumentException("Database type must be provided")
        val db = databaseName ?: throw IllegalArgumentException("Database name must be provided")
        val user = username ?: throw IllegalArgumentException("Username must be provided")
        val pass = password ?: throw IllegalArgumentException("Password must be provided")
        val query = query ?: throw IllegalArgumentException("Query must be provided")

        // For SQLite, the host is not needed.
        if (type == SQLDataSource.DatabaseType.SQLITE) {
            return SQLDataSource(id, DataSourceType.SQL, type, "", db, user, pass, query)
        }

        val hostValue = host ?: throw IllegalArgumentException("Host must be provided")

        return SQLDataSource(id, DataSourceType.SQL, type, hostValue, db, user, pass, query)
    }
}
