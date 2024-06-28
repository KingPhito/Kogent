package com.ralphdugue.kogent.dataconnector.domain.entities

import com.ralphdugue.kogent.indexing.domain.entities.KogentDocument

/**
 * This is the interface for all DataConnectors classes to inherit from.
 * DataConnectors are classes that are responsible for sending queries to a data source.
 */
interface KogentDataConnector<T> {
    /**
     * This function fetches data from a data source.
     * @param dataSource The data source to fetch data from.
     * @return The data fetched from the data source.
     */
    suspend fun fetchData(dataSource: KogentDataSource): T

    /**
     * This function creates a document from the data. The document is used to store data in an index.
     * @param data The data to create the document from.
     * @param source The data source the data came from.
     * @return The document created from the data.
     */
    suspend fun createDocument(
        data: T,
        source: KogentDataSource,
    ): KogentDocument
}
