package com.ralphdugue.kogent.indexing.domain.entities

enum class IndexType {
    VectorDatabase,
    InvertedIndex,
    HybridIndex,
}

enum class VectorDatabaseOptions { MILVUS, OPEN_SEARCH, COTTONTAIL_DB }

sealed interface IndexConfig {
    val type: IndexType

    data class VectorDatabaseConfig(
        override val type: IndexType = IndexType.VectorDatabase,
        val vectorDatabaseType: VectorDatabaseOptions,
        val connectionString: String,
        val credentials: List<Map<String, String>> = emptyList(),
    ) : IndexConfig
}
