package com.ralphdugue.kogent.indexing.utils

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.KogentIndex
import com.ralphdugue.kogent.indexing.domain.entities.KogentIndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.KogentIndexType
import com.ralphdugue.kogent.indexing.domain.entities.VectorDatabaseType

object KogentIndexFactory {
    fun createIndex(config: KogentIndexConfig): KogentIndex =
        when (config.type) {
            KogentIndexType.VectorDatabase -> {
                val vectorDatabaseConfig = config as KogentIndexConfig.KogentVectorDatabaseConfig
                when (vectorDatabaseConfig.vectorDatabaseType) {
                    VectorDatabaseType.MILVUS -> MilvusIndex(vectorDatabaseConfig)
                    VectorDatabaseType.OPEN_SEARCH -> TODO()
                    VectorDatabaseType.COTTONTAIL_DB -> TODO()
                }
            }
            KogentIndexType.InvertedIndex -> TODO()
            KogentIndexType.HybridIndex -> TODO()
        }
}
