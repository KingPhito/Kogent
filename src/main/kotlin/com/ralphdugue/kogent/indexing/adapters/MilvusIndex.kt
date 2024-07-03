package com.ralphdugue.kogent.indexing.adapters

import com.alibaba.fastjson.JSONObject
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig.VectorDatabaseConfig
import io.milvus.param.MetricType
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.collection.request.CreateCollectionReq
import io.milvus.v2.service.collection.request.GetLoadStateReq
import io.milvus.v2.service.collection.request.HasCollectionReq
import io.milvus.v2.service.collection.request.LoadCollectionReq
import io.milvus.v2.service.vector.request.DeleteReq
import io.milvus.v2.service.vector.request.InsertReq
import io.milvus.v2.service.vector.request.UpsertReq

class MilvusIndex(
    config: VectorDatabaseConfig,
    private val client: MilvusClientV2,
) : Index {
    override suspend fun indexDocument(document: Document): Boolean =
        try {
            if (!loadCollection(document.sourceName)) {
                if (!createCollection(document)) {
                    throw Exception("Failed to create collection")
                }
            }
            val data = createData(document)
            val response =
                client.insert(
                    InsertReq
                        .builder()
                        .collectionName(document.sourceName)
                        .data(data)
                        .build(),
                )
            response.insertCnt == data.size.toLong()
        } catch (e: Exception) {
            false
        }

    override suspend fun searchIndex(
        query: FloatArray,
        topK: Int,
    ): List<Document> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDocument(document: Document): Boolean =
        try {
            val response =
                client.delete(
                    DeleteReq
                        .builder()
                        .collectionName(document.sourceName)
                        .ids(listOf(document.id))
                        .build(),
                )
            response.deleteCnt == 1L
        } catch (e: Exception) {
            false
        }

    override suspend fun updateDocument(data: Document): Boolean =
        try {
            val response =
                client.upsert(
                    UpsertReq
                        .builder()
                        .collectionName(data.sourceName)
                        .data(createData(data))
                        .build(),
                )
            response.upsertCnt == 1L
        } catch (e: Exception) {
            false
        }

    private fun createData(document: Document): List<JSONObject> {
        val data =
            JSONObject()
                .fluentPut("id", document.id)
                .fluentPut("sourceName", document.sourceName)
                .fluentPut("sourceType", document.sourceType)
                .fluentPut("embedding", document.embedding)
        return when (document) {
            is Document.SQLDocument -> {
                data.put("dialect", document.dialect)
                listOf(data)
            }
            is Document.APIDocument -> TODO()
        }
    }

    private fun loadCollection(collectionName: String): Boolean {
        val hasCollectionRequest =
            HasCollectionReq
                .builder()
                .collectionName(collectionName)
                .build()
        val hasCollection = client.hasCollection(hasCollectionRequest)
        return if (hasCollection) {
            val loadCollectionRequest =
                LoadCollectionReq
                    .builder()
                    .collectionName(collectionName)
                    .build()
            client.loadCollection(loadCollectionRequest)
            hasCollection
        } else {
            false
        }
    }

    private fun createCollection(document: Document): Boolean {
        client.createCollection(
            CreateCollectionReq
                .builder()
                .collectionName(document.sourceName)
                .dimension(document.embedding.size)
                .metricType(MetricType.L2.name)
                .build(),
        )
        return client.getLoadState(GetLoadStateReq.builder().collectionName(document.sourceName).build())
    }
}
