package dataconnector.domain.entities

/**
 * This class represents an SQL data source.
 * @param identifier The identifier of the data source.
 * @param host The URL of the SQL database.
 * @param port The port of the SQL database.
 * @param databaseName The name of the SQL database.
 * @param username The username to use to connect to the SQL database.
 * @param password The password to use to connect to the SQL database.
 * @param query The query to retrieve data from the SQL database.
 */
data class KogentSQLDataSource(
    override val identifier: String,
    val databaseType: DatabaseType,
    val host: String,
    val port: Int,
    val databaseName: String,
    val username: String,
    val password: String,
    val query: String,
) : KogentDataSource {
    /**
     * This enum represents the type of the database.
     */
    enum class DatabaseType {
        MYSQL,
        POSTGRESQL,
        SQLITE,
    }
}
