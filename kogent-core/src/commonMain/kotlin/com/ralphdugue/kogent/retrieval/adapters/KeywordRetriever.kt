package com.ralphdugue.kogent.retrieval.adapters

import co.touchlab.kermit.Logger
import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import com.ralphdugue.kogent.retrieval.utils.RetrieverUtils
import kotlinx.coroutines.*
import org.koin.core.annotation.Single

@Single
class KeywordRetriever(
    private val index: Index,
    private val embeddingModel: EmbeddingModel,
    private val dataSourceRegistry: DataSourceRegistry,
) : Retriever {
    override suspend fun retrieve(query: String, maxSources: Long): Result<List<Document>> {
        return runCatching {
            val collectionNames = selectDataSources(query, maxSources)
            embeddingModel.getEmbedding(query).fold(
                onSuccess = { embedding ->
                    getDocuments(collectionNames, embedding)
                },
                onFailure = { throw it }
            )
        }
    }

    private suspend fun getDocuments(collectionNames: Set<String>, embedding: List<Float>): List<Document> {
        return supervisorScope {
            collectionNames.map { collectionName ->
                async(Dispatchers.IO) {
                    try {
                        index.searchIndex(collectionName, embedding)
                    } catch (e: Exception) {
                        Logger.e(e) { "Failed to retrieve documents for collection: $collectionName" }
                        emptyList()
                    }
                }
            }.awaitAll().flatten() // Flatten the list of lists
        }
    }

    private suspend fun selectDataSources(query: String, maxSources: Long): Set<String> {
        val lowercaseQuery = query.lowercase()
        val dataSourceNames = mutableSetOf<String>()
        dataSourceRegistry.getDataSources().fold(
            onSuccess = { dataSources ->
                dataSources.forEachIndexed { index, dataSource ->
                    if (lowercaseQuery.contains(dataSource.identifier.lowercase())) {
                        dataSourceNames.add(
                            when (dataSource) {
                                is SQLDataSource -> dataSource.databaseName
                                is APIDataSource -> dataSource.baseUrl
                            }
                        )
                    }
                    if (index >= maxSources - 1) {
                        return dataSourceNames
                    }
                }
                return dataSourceNames
            },
            onFailure = { throw it }
        )
    }
}
