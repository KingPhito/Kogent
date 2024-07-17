package com.ralphdugue.kogent.data.domain.entities

/**
 * This is the interface for all DataConnectors classes to inherit from.
 * DataConnectors are classes that are responsible for sending queries to a data source.
 */
interface DataConnector {
    /**
     * This function fetches data from a data source, and adds it to the index.
     * @param dataSource The data source to fetch data from.
     * @return The result of the index operation.
     */
    suspend fun indexData(dataSource: DataSource): Result<Unit>

    /**
     * This function updates the data of a data source.
     * @param dataSource The data source to update the data of.
     * @return The result of the query.
     */
    suspend fun updateData(dataSource: DataSource): Result<Unit>

    /**
     * This function deletes a data source from the index.
     * @param dataSource The data source to delete data.
     * @return The result of the delete operation.
     */
    suspend fun removeData(dataSource: DataSource): Result<Unit>

}
