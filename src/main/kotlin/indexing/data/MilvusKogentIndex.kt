package indexing.data

import indexing.domain.entities.KogentDocument
import indexing.domain.entities.KogentIndex
import indexing.domain.entities.KogentIndexConfig.KogentVectorDatabaseConfig
import io.milvus.v2.client.ConnectConfig
import io.milvus.v2.client.MilvusClientV2

class MilvusKogentIndex(
    config: KogentVectorDatabaseConfig,
) : KogentIndex {
    private var client: MilvusClientV2

    init {
        val milvusConfig =
            ConnectConfig
                .builder()
                .uri(config.connectionString)
                .build()
        client = MilvusClientV2(milvusConfig)
    }

    override suspend fun indexData(data: KogentDocument): Boolean {
        TODO()
    }

    override suspend fun searchData(
        query: String,
        topK: Int,
    ): List<KogentDocument> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteData(query: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateData(
        query: String,
        data: KogentDocument,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
