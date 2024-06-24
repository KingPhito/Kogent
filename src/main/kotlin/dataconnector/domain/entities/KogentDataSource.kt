package dataconnector.domain.entities

/**
 * This is the interface for all DataSources to inherit from.
 * DataSources are classes that represent anything from an SQL database to a REST API.
 */
sealed interface DataSource {
    /**
     * The identifier of the data source.
     */
    val identifier: String
}

enum class DatabaseType {
    MYSQL,
    POSTGRESQL,
    SQLITE,
}

/**
 * This class represents an SQL data source.
 * @param identifier The identifier of the data source.
 * @param host The URL of the SQL database.
 * @param port The port of the SQL database.
 * @param databaseName The name of the SQL database.
 * @param username The username to use to connect to the SQL database.
 * @param password The password to use to connect to the SQL database.
 */
data class SQLDataSource(
    override val identifier: String,
    val databaseType: DatabaseType,
    val host: String,
    val port: Int,
    val databaseName: String,
    val username: String,
    val password: String,
) : DataSource
