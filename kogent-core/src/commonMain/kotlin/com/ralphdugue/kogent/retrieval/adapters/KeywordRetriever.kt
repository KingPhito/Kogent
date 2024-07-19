package com.ralphdugue.kogent.retrieval.adapters

import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import org.koin.core.annotation.Single

@Single
class KeywordRetriever(
    private val index: Index,
    private val embeddingModel: EmbeddingModel,
    private val dataSourceRegistry: DataSourceRegistry,
) : Retriever {
    override suspend fun retrieve(query: String): Result<String> {
        return runCatching {
            val dataSourceName = selectDataSource(query)
            embeddingModel.getEmbedding(query).fold(
                onSuccess = { embedding ->
                    val documents = index.searchIndex(dataSourceName, embedding)
                    buildContext(query, documents)
                },
                onFailure = { throw it }
            )
        }
    }

    private suspend fun selectDataSource(query: String): String {
        val lowercaseQuery = query.lowercase()
        dataSourceRegistry.getDataSources().fold(
            onSuccess = { dataSources ->
                dataSources.forEach { dataSource ->
                    if (lowercaseQuery.contains(dataSource.identifier.lowercase())) {
                        return dataSource.identifier
                    }
                }
                return dataSources.first().identifier
            },
            onFailure = { throw it }
        )
    }

    private fun buildContext(
        query: String,
        documents: List<Document>,
    ): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("User request: $query\n")
        documents.forEach { document ->
            stringBuilder.appendLine("Document: ${document.id}")
            stringBuilder.appendLine("SourceType: ${document.sourceType}")
            stringBuilder.appendLine("SourceName: ${document.sourceName}")
            when (document) {
                is Document.SQLDocument -> {
                    stringBuilder.appendLine("Dialect: ${document.dialect}")
                    stringBuilder.appendLine("Schema: ${document.schema}")
                    stringBuilder.appendLine("Query: ${document.query}")
                }
                is Document.APIDocument -> {
                    // No additional information for API documents
                }
            }
            stringBuilder.appendLine("Text: ${document.text}")
            stringBuilder.appendLine("Embedding: ${document.embedding}")
            stringBuilder.appendLine("-----------------------------")
        }
        return stringBuilder.toString()
    }
}
