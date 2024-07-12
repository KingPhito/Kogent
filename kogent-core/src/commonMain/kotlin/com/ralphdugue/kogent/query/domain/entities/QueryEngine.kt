package com.ralphdugue.kogent.query.domain.entities

import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever

/**
 * A query engine.
 *
 * The query engine is the main interface for the Kogent system. It coordinates the entire
 * RAG-based pipeline, from the user query to the response generation. It is responsible for
 * requesting context from the [Retriever], sending user queries to the [LLModel] for response
 * generation, for generating responses from the [LLMResponse], and for managing the data sources.
 * @author Ralph Dugue
 */
interface QueryEngine {
    suspend fun processQuery(query: String): String

    suspend fun addDataSource(dataSource: DataSource): Boolean

    suspend fun removeDataSource(dataSource: DataSource): Boolean

    suspend fun updateDataSource(dataSource: DataSource): Boolean
}
