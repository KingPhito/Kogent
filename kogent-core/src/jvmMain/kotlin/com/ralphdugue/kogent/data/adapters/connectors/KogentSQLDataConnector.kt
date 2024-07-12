package com.ralphdugue.kogent.data.adapters.connectors

import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.sql.QueryResult
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSource.DatabaseType
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.ResultSet

actual fun buildSQLDataConnector(
    embeddingModel: EmbeddingModel,
    index: Index,
    dataSourceRegistry: DataSourceRegistry,
): SQLDataConnector = KogentSQLDataConnector(embeddingModel, index, dataSourceRegistry)

class KogentSQLDataConnector(
    private val embeddingModel: EmbeddingModel,
    private val index: Index,
    private val dataSourceRegistry: DataSourceRegistry,
) : SQLDataConnector {
    override suspend fun indexData(dataSource: DataSource): Boolean {
        val schemaQuery = fetchSchema(dataSource as SQLDataSource)
        val tableQuery = readQuery(dataSource)
        val tableDocument = createDocument(tableQuery, schemaQuery, dataSource)
        val tableIndexed = index.indexDocument(tableDocument)
        if (tableIndexed) {
            dataSourceRegistry.registerDataSource(dataSource)
        }
        return tableIndexed
    }

    override suspend fun updateData(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeData(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun readQuery(dataSource: DataSource): QueryResult.TableQuery {
        if (dataSource !is SQLDataSource) {
            return QueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = QueryResult.ResultType.FAILURE,
            )
        }
        if (dataSource.query == null) {
            return QueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = QueryResult.ResultType.FAILURE,
            )
        }
        val connection = getConnection(dataSource)
        return executeQuery(connection, dataSource.query).use { resultSet ->
            val columnNames = mutableSetOf<String>()
            val rows = mutableListOf<Map<String, Any?>>()
            val tableName = resultSet.metaData.getTableName(1)
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any?>()
                for (i in 1..resultSet.metaData.columnCount) {
                    columnNames.add(resultSet.metaData.getColumnName(i))
                    row[resultSet.metaData.getColumnName(i)] = resultSet.getObject(i)
                }
                rows.add(row)
            }
            connection.close()
            QueryResult.TableQuery(
                tableName = tableName,
                columnNames = columnNames,
                rows = rows,
                resultType = QueryResult.ResultType.SUCCESS,
            )
        }
    }

    override suspend fun writeQuery(
        dataSource: SQLDataSource,
        query: String,
    ): QueryResult {
        val connection = getConnection(dataSource)
        val rowsUpdated = executeUpdate(connection, query)
        connection.close()
        return if (rowsUpdated > 0) {
            QueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = QueryResult.ResultType.SUCCESS,
            )
        } else {
            QueryResult.TableQuery(
                tableName = "",
                columnNames = emptySet(),
                rows = emptyList(),
                resultType = QueryResult.ResultType.FAILURE,
            )
        }
    }

    private fun fetchSchema(dataSource: SQLDataSource): QueryResult.SchemaQuery {
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
        val result = executeQuery(connection, query)
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
        return QueryResult.SchemaQuery(
            schema = schema,
            resultType = QueryResult.ResultType.SUCCESS,
        )
    }

    private suspend fun createDocument(
        tableQuery: QueryResult.TableQuery,
        schemaQuery: QueryResult.SchemaQuery,
        source: DataSource,
    ): Document {
        if (source !is SQLDataSource) {
            throw IllegalArgumentException("Data source is not an SQL data source.")
        }
        if (tableQuery.resultType == QueryResult.ResultType.FAILURE) {
            throw IllegalArgumentException("Cannot create document from failed query result")
        }
        return Document.SQLDocument(
            id = source.identifier,
            sourceType = "SQL",
            sourceName = source.databaseName,
            dialect = source.databaseType.name,
            schema = schemaQuery.toString(),
            embedding = embeddingModel.getEmbedding(tableQuery.toString()),
        )
    }

    private fun executeQuery(
        connection: Connection,
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
        query: String,
    ): Int =
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(query)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to execute query: ${e.message}")
        }

    private fun getConnection(dataSource: SQLDataSource): Connection =
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
