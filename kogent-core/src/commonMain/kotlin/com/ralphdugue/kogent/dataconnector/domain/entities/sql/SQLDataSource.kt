package com.ralphdugue.kogent.dataconnector.domain.entities.sql

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource

/**
 * This class represents an SQL data source.
 * @param identifier The identifier of the data source.
 * @param host The URL of the SQL database.
 * @param port The port of the SQL database.
 * @param databaseName The name of the SQL database.
 * @param username The username to use to connect to the SQL database.
 * @param password The password to use to connect to the SQL database.
 * @param query The query to execute on the SQL database.(optional)
 */
data class SQLDataSource(
    override val identifier: String,
    val databaseType: DatabaseType,
    val host: String,
    val databaseName: String,
    val username: String,
    val password: String,
    val query: String? = null,
) : DataSource {
    /**
     * This enum represents the type of the database.
     */
    enum class DatabaseType {
        MYSQL,
        POSTGRESQL,
        SQLITE,
        H2,
    }
}
