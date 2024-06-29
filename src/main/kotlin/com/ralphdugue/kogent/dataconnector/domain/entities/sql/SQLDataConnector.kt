package com.ralphdugue.kogent.dataconnector.domain.entities.sql

import com.ralphdugue.kogent.dataconnector.domain.entities.DataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource

/**
 * This is the interface for all SQLDataConnectors classes to inherit from.
 * SQLDataConnectors are classes that are responsible for sending queries to an SQL data source.
 */
interface SQLDataConnector : DataConnector<QueryResult> {
    /**
     * This function fetches data from an SQL data source.
     * @param dataSource The data source to fetch data from.
     * @return The result of the query.
     */
    suspend fun fetchData(dataSource: DataSource): QueryResult.TableQuery

    /**
     * This function updates the data of an SQL data source.
     * @param dataSource The data source to update the data of.
     * @param query The query to update the data.
     * @return The result of the query.
     */
    suspend fun updateData(
        dataSource: SQLDataSource,
        query: String,
    ): QueryResult

    /**
     * This function fetches the schema of an SQL data source.
     * @param dataSource The data source to fetch the schema from.
     * @return The schema of the data source.
     */
    suspend fun fetchSchema(dataSource: SQLDataSource): QueryResult.SchemaQuery
}
