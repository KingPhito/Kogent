package dataconnector.data

import com.zaxxer.hikari.HikariDataSource
import dataconnector.domain.entities.KogentDataSource
import dataconnector.domain.entities.KogentQueryResult
import dataconnector.domain.entities.KogentSQLDataConnector
import dataconnector.domain.entities.KogentSQLDataSource
import dataconnector.domain.entities.KogentSQLDataSource.DatabaseType
import java.sql.Connection

class SQLDataConnectorImpl : KogentSQLDataConnector {
    override suspend fun fetchData(dataSource: KogentDataSource): KogentQueryResult {
        if (dataSource !is KogentSQLDataSource) {
            return KogentQueryResult(emptyList(), emptyList(), KogentQueryResult.ResultType.FAILURE)
        }
        return executeQuery(dataSource, dataSource.query)
    }

    override suspend fun executeQuery(
        dataSource: KogentSQLDataSource,
        query: String,
    ): KogentQueryResult =
        try {
            val connection = getConnection(dataSource)
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)
            val metaData = resultSet.metaData
            val columnNames = (1..metaData.columnCount).map { metaData.getColumnName(it) }
            val rows = mutableListOf<Map<String, Any?>>()
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any?>()
                columnNames.forEach { row[it] = resultSet.getObject(it) }
                rows.add(row)
            }
            KogentQueryResult(columnNames, rows)
        } catch (e: Exception) {
            KogentQueryResult(emptyList(), emptyList(), KogentQueryResult.ResultType.FAILURE)
        }

    private fun getConnection(dataSource: KogentSQLDataSource): Connection =
        when (dataSource.databaseType) {
            DatabaseType.MYSQL -> TODO()
            DatabaseType.POSTGRESQL -> {
                val hikariDataSource = HikariDataSource()
                hikariDataSource.jdbcUrl = "jdbc:postgresql://${dataSource.host}:${dataSource.port}/${dataSource.databaseName}"
                hikariDataSource.username = dataSource.username
                hikariDataSource.password = dataSource.password
                hikariDataSource.connection
            }
            DatabaseType.SQLITE -> TODO()
        }
}
