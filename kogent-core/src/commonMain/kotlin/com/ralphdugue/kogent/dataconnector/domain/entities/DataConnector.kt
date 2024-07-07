package com.ralphdugue.kogent.dataconnector.domain.entities

/**
 * This is the interface for all DataConnectors classes to inherit from.
 * DataConnectors are classes that are responsible for sending queries to a data source.
 */
interface DataConnector<T> {
    /**
     * This function fetches data from a data source, and adds it to the index.
     * @param dataSource The data source to fetch data from.
     * @return The result of the index operation.
     */
    suspend fun indexData(dataSource: DataSource): Boolean
}
