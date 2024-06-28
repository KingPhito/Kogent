package com.ralphdugue.kogent.indexing.domain.entities

/**
 * This is the interface for all Indexes to inherit from.
 * Indexes store data from a [KogentDocument] in a way that makes it easy to search and retrieve.
 */
interface KogentIndex {
    /**
     * This function inserts data from a [KogentDocument] into the index.
     * @param data The data to index.
     * @return True if the data was successfully indexed, false otherwise.
     */
    suspend fun indexData(data: KogentDocument): Boolean

    /**
     * This function searches the index for data that matches the query.
     * @param query The query to search for.
     * @param topK The number of results to return.
     * @return The data that matches the query.
     */
    suspend fun searchData(
        query: String,
        topK: Int = 5,
    ): List<KogentDocument>

    /**
     * This function deletes data from the index that matches the query.
     * @param query The query to delete data for.
     * @return True if the data was successfully deleted, false otherwise.
     */
    suspend fun deleteData(query: String): Boolean

    /**
     * This function updates data in the index that matches the query.
     * @param query The query to update data for.
     * @param data The new data to update with.
     * @return True if the data was successfully updated, false otherwise.
     */
    suspend fun updateData(
        query: String,
        data: KogentDocument,
    ): Boolean
}
