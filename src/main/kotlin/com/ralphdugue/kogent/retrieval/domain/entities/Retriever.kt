package com.ralphdugue.kogent.retrieval.domain.entities

interface Retriever {
    suspend fun retrieve(
        query: String,
        sourceName: String,
    ): String
}
