package com.ralphdugue.kogent.indexing.domain.entities

/**
 * This is the interface for all Indexes to inherit from.
 * Indexes store data from a [Document] in a way that makes it easy to search and retrieve.
 */
interface Index {
    /**
     * This function inserts data from a [Document] into the index.
     * @param document The data to index.
     * @return True if the data was successfully indexed, false otherwise.
     */
    suspend fun indexDocument(document: Document): Boolean

    /**
     * This function searches the index for data that matches the query.
     * @param query The query to search for.
     * @param topK The number of results to return.
     * @return The data that matches the query.
     */
    suspend fun searchIndex(
        query: FloatArray,
        topK: Int = 5,
    ): List<Document>

    /**
     * This function deletes data from the index that matches the query.
     * @param document The data to delete.
     * @return True if the data was successfully deleted, false otherwise.
     */
    suspend fun deleteDocument(document: Document): Boolean

    /**
     * This function updates data in the index that matches the query.
     * @param data The new data to update with.
     * @return True if the data was successfully updated, false otherwise.
     */
    suspend fun updateDocument(data: Document): Boolean
}
