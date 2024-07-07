package com.ralphdugue.kogent.indexing.domain.entities

enum class IndexType {
    VectorStore,
    InvertedIndex,
    HybridIndex,
}

enum class VectorStoreOptions { MILVUS, OPEN_SEARCH, COTTONTAIL_DB }

sealed interface IndexConfig {
    val type: IndexType

    data class VectorStoreConfig(
        override val type: IndexType = IndexType.VectorStore,
        val vectorDatabaseType: VectorStoreOptions,
        val connectionString: String,
        val extraParams: Map<String, String> = emptyMap(),
    ) : IndexConfig
}
