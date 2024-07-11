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
    override suspend fun retrieve(query: String): String {
        val sourceName = selectDataSource(query)
        val documents =
            index.searchIndex(
                sourceName = sourceName,
                query = embeddingModel.getEmbedding(query),
            )
        return buildContext(query, documents)
    }

    private suspend fun selectDataSource(query: String): String {
        val lowercaseQuery = query.lowercase()
        val dataSources = dataSourceRegistry.getDataSources()
        for (dataSource in dataSources) {
            if (lowercaseQuery.contains(dataSource.lowercase())) {
                return dataSource
            }
        }
        return dataSources.first()
    }

    private fun buildContext(
        query: String,
        documents: List<Document>,
    ): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("Query: $query\n")
        documents.forEach { document ->
            stringBuilder.appendLine("Document: ${document.id}")
            stringBuilder.appendLine("SourceType: ${document.sourceType}")
            stringBuilder.appendLine("SourceName: ${document.sourceName}")
            when (document) {
                is Document.SQLDocument -> {
                    stringBuilder.appendLine("Dialect: ${document.dialect}")
                    stringBuilder.appendLine("Schema: ${document.schema}")
                }
                is Document.APIDocument -> {
                    // No additional information for API documents
                }
            }
            stringBuilder.appendLine("Embedding: ${document.embedding}")
            stringBuilder.appendLine("-----------------------------")
        }
        return stringBuilder.toString()
    }
}
