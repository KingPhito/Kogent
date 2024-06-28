package com.ralphdugue.kogent.indexing.domain.entities

enum class KogentIndexType {
    VectorDatabase,
    InvertedIndex,
    HybridIndex,
}

enum class VectorDatabaseType { MILVUS, OPEN_SEARCH, COTTONTAIL_DB }

sealed interface KogentIndexConfig {
    val type: KogentIndexType

    data class KogentVectorDatabaseConfig(
        override val type: KogentIndexType = KogentIndexType.VectorDatabase,
        val vectorDatabaseType: VectorDatabaseType,
        val connectionString: String,
        val credentials: List<Map<String, String>>,
    ) : KogentIndexConfig
}
