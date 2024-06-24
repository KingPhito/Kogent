package dataconnector.domain.entities

/**
 * This is the interface for all SQLDataConnectors classes to inherit from.
 * SQLDataConnectors are classes that are responsible for sending queries to an SQL data source.
 */
interface KogentSQLDataConnector : KogentDataConnector {
    /**
     * This function executes a query on an SQL data source.
     * @param dataSource The data source to execute the query on.
     * @param query The query to execute.
     * @return The result of the query.
     */
    suspend fun executeQuery(
        dataSource: KogentSQLDataSource,
        query: String,
    ): KogentQueryResult
}
