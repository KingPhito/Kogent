package com.ralphdugue.kogent.indexing.utils

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorDatabaseOptions

object IndexFactory {
    fun createIndex(config: IndexConfig) =
        when (config) {
            is IndexConfig.VectorDatabaseConfig -> {
                when (config.vectorDatabaseType) {
                    VectorDatabaseOptions.MILVUS -> MilvusIndex(config)
                    VectorDatabaseOptions.OPEN_SEARCH -> TODO()
                    VectorDatabaseOptions.COTTONTAIL_DB -> TODO()
                }
            }
        }
}
