package com.ralphdugue.kogent.indexing.utils

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorDatabaseOptions
import io.milvus.v2.client.ConnectConfig
import io.milvus.v2.client.MilvusClientV2

object IndexFactory {
    fun createIndex(config: IndexConfig) =
        when (config) {
            is IndexConfig.VectorDatabaseConfig -> {
                when (config.vectorDatabaseType) {
                    VectorDatabaseOptions.MILVUS -> {
                        MilvusIndex(
                            config = config,
                            client =
                                MilvusClientV2(
                                    ConnectConfig
                                        .builder()
                                        .uri(config.connectionString)
                                        .build(),
                                ),
                        )
                    }

                    VectorDatabaseOptions.OPEN_SEARCH -> TODO()
                    VectorDatabaseOptions.COTTONTAIL_DB -> TODO()
                }
            }
        }
}
