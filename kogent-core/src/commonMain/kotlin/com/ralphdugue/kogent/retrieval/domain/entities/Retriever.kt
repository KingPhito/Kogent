package com.ralphdugue.kogent.retrieval.domain.entities

/**
 * Retrievers are classes that are used to retrieve data from an index.
 * They analyze the query and determine which index to query from.
 */
interface Retriever {
    /**
     * This function retrieves data from an index.
     * @param query the query to retrieve data for
     * @return a string that represents the context for a [QueryEngine] to use.
     */
    suspend fun retrieve(query: String): String
}
