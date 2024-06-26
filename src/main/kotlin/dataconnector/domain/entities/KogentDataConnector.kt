package dataconnector.domain.entities

import indexing.domain.entities.KogentDocument

/**
 * This is the interface for all DataConnectors classes to inherit from.
 * DataConnectors are classes that are responsible for sending queries to a data source.
 */
sealed interface KogentDataConnector {
    /**
     * This function fetches data from a data source.
     * @param dataSource The data source to fetch data from.
     * @return The data fetched from the data source.
     */
    suspend fun fetchData(dataSource: KogentDataSource): KogentQueryResult

    fun createDocument(
        data: KogentQueryResult,
        source: KogentDataSource,
    ): KogentDocument
}
