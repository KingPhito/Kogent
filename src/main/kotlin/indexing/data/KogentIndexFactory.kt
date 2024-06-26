package indexing.data

import indexing.domain.entities.KogentIndex
import indexing.domain.entities.KogentIndexConfig
import indexing.domain.entities.KogentIndexType
import indexing.domain.entities.VectorDatabaseType

object KogentIndexFactory {
    fun createIndex(config: KogentIndexConfig): KogentIndex =
        when (config.type) {
            KogentIndexType.VectorDatabase -> {
                val vectorDatabaseConfig = config as KogentIndexConfig.KogentVectorDatabaseConfig
                when (vectorDatabaseConfig.vectorDatabaseType) {
                    VectorDatabaseType.MILVUS -> TODO()
                    VectorDatabaseType.OPEN_SEARCH -> TODO()
                    VectorDatabaseType.COTTONTAIL_DB -> TODO()
                }
            }
            KogentIndexType.InvertedIndex -> TODO()
            KogentIndexType.HybridIndex -> TODO()
        }
}
