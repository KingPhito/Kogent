package com.ralphdugue.kogent.retrieval.domain.entities

import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.query.domain.entities.QueryEngine

/**
 * A context retriever.
 *
 * Retrievers are classes that are used to retrieve data from an [Index].
 * They analyze the query and determine which index to query from.
 * They then send the query to the index and retrieve the data.
 * @author Ralph Dugue
 */
interface Retriever {
    /**
     * This function retrieves data from an index.
     * @param query the query to retrieve data for.
     * @param maxSources the maximum number of sources to retrieve data from.
     * @return a string that represents the context for a [QueryEngine] to use.
     */
    suspend fun retrieve(query: String, maxSources: Long = 100): Result<List<Document>>
}
