package dataconnector.data

import com.zaxxer.hikari.HikariDataSource
import dataconnector.domain.entities.KogentDataSource
import dataconnector.domain.entities.KogentQueryResult
import dataconnector.domain.entities.KogentSQLDataConnector
import dataconnector.domain.entities.KogentSQLDataSource
import dataconnector.domain.entities.KogentSQLDataSource.DatabaseType
import indexing.domain.entities.KogentDocument
import java.sql.Connection

class SQLDataConnectorImpl : KogentSQLDataConnector {
    override suspend fun fetchData(dataSource: KogentDataSource): KogentQueryResult {
        if (dataSource !is KogentSQLDataSource) {
            return KogentQueryResult(emptyList(), emptyList(), KogentQueryResult.ResultType.FAILURE)
        }
        dataSource.query?.let {
            return executeQuery(dataSource, it)
        } ?: return KogentQueryResult(emptyList(), emptyList(), KogentQueryResult.ResultType.FAILURE)
    }

    override suspend fun fetchSchema(dataSource: KogentSQLDataSource): KogentQueryResult {
        val query =
            when (dataSource.databaseType) {
                DatabaseType.MYSQL -> "SHOW TABLES"
                DatabaseType.POSTGRESQL -> "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"
                DatabaseType.SQLITE -> "SELECT name FROM sqlite_master WHERE type='table'"
                DatabaseType.H2 -> "SHOW TABLES"
            }
        return executeQuery(dataSource, query)
    }

    override fun createDocument(
        data: KogentQueryResult,
        source: KogentDataSource,
    ): KogentDocument {
        if (source !is KogentSQLDataSource) {
            throw IllegalArgumentException("Data source must be an SQL data source")
        }
        if (data.resultType == KogentQueryResult.ResultType.FAILURE) {
            throw IllegalArgumentException("Cannot create document from failed query result")
        }
        return KogentDocument.KogentSQLDocument(
            id = source.identifier,
            sourceType = "SQL",
            sourceName = source.databaseName,
            content = data,
        )
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
            DatabaseType.MYSQL -> {
                val hikariDataSource = HikariDataSource()
                hikariDataSource.jdbcUrl = "jdbc:mysql://${dataSource.host}/${dataSource.databaseName}"
                hikariDataSource.username = dataSource.username
                hikariDataSource.password = dataSource.password
                hikariDataSource.connection
            }
            DatabaseType.POSTGRESQL -> {
                val hikariDataSource = HikariDataSource()
                hikariDataSource.jdbcUrl = "jdbc:postgresql://${dataSource.host}/${dataSource.databaseName}"
                hikariDataSource.username = dataSource.username
                hikariDataSource.password = dataSource.password
                hikariDataSource.connection
            }
            DatabaseType.SQLITE -> {
                val hikariDataSource = HikariDataSource()
                hikariDataSource.jdbcUrl = "jdbc:sqlite:${dataSource.host}"
                hikariDataSource.connection
            }
            DatabaseType.H2 -> {
                val hikariDataSource = HikariDataSource()
                hikariDataSource.jdbcUrl = "jdbc:h2:${dataSource.host}:${dataSource.databaseName}"
                hikariDataSource.username = dataSource.username
                hikariDataSource.password = dataSource.password
                hikariDataSource.connection
            }
        }
}
