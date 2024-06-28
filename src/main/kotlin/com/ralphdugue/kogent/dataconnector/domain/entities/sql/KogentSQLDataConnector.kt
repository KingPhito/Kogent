package com.ralphdugue.kogent.dataconnector.domain.entities.sql

import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataSource

/**
 * This is the interface for all SQLDataConnectors classes to inherit from.
 * SQLDataConnectors are classes that are responsible for sending queries to an SQL data source.
 */
interface KogentSQLDataConnector : KogentDataConnector<KogentQueryResult> {
    override suspend fun fetchData(dataSource: KogentDataSource): KogentQueryResult.TableQuery

    /**
     * This function updates the data of an SQL data source.
     * @param dataSource The data source to update the data of.
     * @param query The query to update the data.
     * @return The result of the query.
     */
    suspend fun updateData(
        dataSource: KogentSQLDataSource,
        query: String,
    ): KogentQueryResult

    /**
     * This function fetches the schema of an SQL data source.
     * @param dataSource The data source to fetch the schema from.
     * @return The schema of the data source.
     */
    suspend fun fetchSchema(dataSource: KogentSQLDataSource): KogentQueryResult.SchemaQuery
}
