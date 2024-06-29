package com.ralphdugue.kogent.indexing.adapters

import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig.VectorDatabaseConfig
import io.milvus.v2.client.ConnectConfig
import io.milvus.v2.client.MilvusClientV2

class MilvusIndex(
    config: VectorDatabaseConfig,
) : Index {
    private var client: MilvusClientV2

    init {
        val milvusConfig =
            ConnectConfig
                .builder()
                .uri(config.connectionString)
                .build()
        client = MilvusClientV2(milvusConfig)
    }

    override suspend fun indexData(data: Document): Boolean {
        TODO()
    }

    override suspend fun searchData(
        query: String,
        topK: Int,
    ): List<Document> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteData(query: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateData(
        query: String,
        data: Document,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
