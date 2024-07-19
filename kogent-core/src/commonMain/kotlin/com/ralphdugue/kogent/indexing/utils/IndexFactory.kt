package com.ralphdugue.kogent.indexing.utils

import com.ralphdugue.kogent.indexing.adapters.buildMilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreOptions

object IndexFactory {
    fun createIndex(config: IndexConfig) =
        when (config) {
            is VectorStoreConfig -> {
                when (config.vectorDatabaseType) {
                    VectorStoreOptions.MILVUS -> buildMilvusIndex(config)
                    VectorStoreOptions.OPEN_SEARCH -> TODO()
                    VectorStoreOptions.COTTONTAIL_DB -> TODO()
                }
            }
        }
}
