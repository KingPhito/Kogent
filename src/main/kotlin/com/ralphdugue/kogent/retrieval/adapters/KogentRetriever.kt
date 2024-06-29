package com.ralphdugue.kogent.retrieval.adapters

import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import org.koin.core.annotation.Single

@Single
class KogentRetriever(
    private val index: Index,
) : Retriever {
    override suspend fun retrieve(query: String): String {
        TODO("Not yet implemented")
    }
}
