package com.ralphdugue.kogent.dataconnector.adapters.connectors

import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentEmbeddingModel
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentQueryResult
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentSQLDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentSQLDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.KogentSQLDataSource.DatabaseType
import com.ralphdugue.kogent.indexing.domain.entities.KogentDocument
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.annotation.Single
import java.sql.Connection
import java.sql.ResultSet

@Single(binds = [KogentSQLDataConnector::class])
class KogentSQLDataConnectorImpl(
    private val embeddingModel: KogentEmbeddingModel,
) : KogentSQLDataConnector {
    override suspend fun fetchData(dataSource: KogentDataSource): KogentQueryResult.TableQuery {
        if (dataSource !is KogentSQLDataSource) {
            return KogentQueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = KogentQueryResult.ResultType.FAILURE,
            )
        }
        if (dataSource.query == null) {
            return KogentQueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = KogentQueryResult.ResultType.FAILURE,
            )
        }
        val connection = getConnection(dataSource)
        return executeQuery(connection, dataSource, dataSource.query).use { resultSet ->
            val columnNames = mutableSetOf<String>()
            val rows = mutableListOf<Map<String, Any?>>()
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any?>()
                for (i in 1..resultSet.metaData.columnCount) {
                    columnNames.add(resultSet.metaData.getColumnName(i))
                    row[resultSet.metaData.getColumnName(i)] = resultSet.getObject(i)
                }
                rows.add(row)
            }
            connection.close()
            KogentQueryResult.TableQuery(
                tableName = resultSet.metaData.getTableName(1),
                columnNames = columnNames,
                rows = rows,
                resultType = KogentQueryResult.ResultType.SUCCESS,
            )
        }
    }

    override suspend fun updateData(
        dataSource: KogentSQLDataSource,
        query: String,
    ): KogentQueryResult {
        val connection = getConnection(dataSource)
        val rowsUpdated = executeUpdate(connection, dataSource, query)
        connection.close()
        return if (rowsUpdated > 0) {
            KogentQueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = KogentQueryResult.ResultType.SUCCESS,
            )
        } else {
            KogentQueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = KogentQueryResult.ResultType.FAILURE,
            )
        }
    }

    override suspend fun fetchSchema(dataSource: KogentSQLDataSource): KogentQueryResult.SchemaQuery {
        val query =
            when (dataSource.databaseType) {
                DatabaseType.MYSQL ->
                    """
                    SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME NOT LIKE 'mysql.%' AND TABLE_NAME NOT LIKE 'information_schema.%' AND TABLE_NAME NOT LIKE 'performance_schema.%' AND TABLE_NAME NOT LIKE 'sys.%'
                    """.trimIndent()
                DatabaseType.POSTGRESQL ->
                    """
                    SELECT table_name, column_name, data_type
                    FROM information_schema.columns
                    WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
                    """.trimIndent()
                DatabaseType.SQLITE ->
                    """
                    SELECT tbl_name AS table_name, name AS column_name, type AS data_type
                    FROM sqlite_master
                    JOIN pragma_table_info(sqlite_master.name)
                    WHERE type = 'table' AND tbl_name NOT LIKE 'sqlite_%'
                    """.trimIndent()
                DatabaseType.H2 ->
                    """
                    SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA NOT LIKE 'INFORMATION_SCHEMA'
                    """.trimIndent()
            }
        val connection = getConnection(dataSource)
        val result = executeQuery(connection, dataSource, query)
        val schema = mutableMapOf<String, MutableMap<String, String>>()
        while (result.next()) {
            val tableName = result.getString(1)
            val columnName = result.getString(2)
            val dataType = result.getString(3)
            schema[tableName] =
                schema.getOrDefault(tableName, mutableMapOf()).apply {
                    this[columnName] = dataType
                }
        }
        connection.close()
        return KogentQueryResult.SchemaQuery(
            schema = schema,
            resultType = KogentQueryResult.ResultType.SUCCESS,
        )
    }

    override suspend fun createDocument(
        data: KogentQueryResult,
        source: KogentDataSource,
    ): KogentDocument {
        if (source !is KogentSQLDataSource) {
            throw IllegalArgumentException("Data source is not an SQL data source.")
        }
        if (data.resultType == KogentQueryResult.ResultType.FAILURE) {
            throw IllegalArgumentException("Cannot create document from failed query result")
        }
        return KogentDocument.KogentSQLDocument(
            id = source.identifier,
            sourceType = "SQL",
            sourceName = source.databaseName,
            dialect = source.databaseType.name,
            embedding = embeddingModel.getEmbedding(data.toString()),
        )
    }

    private fun executeQuery(
        connection: Connection,
        dataSource: KogentSQLDataSource,
        query: String,
    ): ResultSet =
        try {
            val statement = connection.createStatement()
            statement.executeQuery(query)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to execute query: ${e.message}")
        }

    private fun executeUpdate(
        connection: Connection,
        dataSource: KogentSQLDataSource,
        query: String,
    ): Int =
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to execute query: ${e.message}")
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
