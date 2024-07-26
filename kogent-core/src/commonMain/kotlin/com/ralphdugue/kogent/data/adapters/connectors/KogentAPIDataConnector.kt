package com.ralphdugue.kogent.data.adapters.connectors

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.api.getResponse
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import io.ktor.client.*
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory(binds = [APIDataConnector::class])
class KogentAPIDataConnector(
    private val client: HttpClient,
    private val embeddingModel: EmbeddingModel,
    private val index: Index,
    private val dataSourceRegistry: DataSourceRegistry,
) : APIDataConnector {
    override suspend fun indexData(dataSource: DataSource): Result<Unit> =
        runCatching {
            val jsonData = fetchData(dataSource as APIDataSource).getOrThrow()
            val document = createDocument(jsonData, dataSource)
            val dataIndexed = index.indexDocument(document)

            if (!dataIndexed) {
                error("Failed to index document")
            }

            dataSourceRegistry.registerDataSource(dataSource)
        }

    override suspend fun updateData(dataSource: DataSource): Result<Unit> =
        runCatching {
            val jsonData = fetchData(dataSource as APIDataSource).getOrThrow()
            val document = createDocument(jsonData, dataSource)
            val dataUpdated = index.updateDocument(document)

            if (!dataUpdated) {
                error("Failed to update document")
            }

            dataSourceRegistry.registerDataSource(dataSource)
        }

    override suspend fun removeData(dataSource: DataSource): Result<Unit> =
        runCatching {
            dataSource as APIDataSource
            val dataRemoved = index.deleteDocument(
                sourceName = dataSource.baseUrl,
                id = dataSource.identifier
            )

            if (!dataRemoved) {
                error("Failed to remove document")
            }

            dataSourceRegistry.removeDataSource(dataSource.identifier)
        }

    override suspend fun fetchData(dataSource: APIDataSource): Result<String> = runCatching {
        getResponse(client, dataSource).getOrThrow()
    }

    override suspend fun postData(
        dataSource: APIDataSource,
        data: String?
    ): Result<String> = runCatching {
        getResponse(client, dataSource.copy(body = data)).fold(
            onSuccess = {
                updateData(dataSource).getOrThrow()
                it
            },
            onFailure = { throw it }
        )
    }

    private suspend fun createDocument(
        data: String,
        source: APIDataSource,
    ): Document.APIDocument {
        val embedding = embeddingModel.getEmbedding(data).fold(
            onSuccess = { it },
            onFailure = { throw it }
        )
        return Document.APIDocument(
            id = source.identifier,
            sourceName = source.baseUrl,
            baseUrl = source.baseUrl,
            endpoint = source.endpoint,
            text = data,
            embedding = embedding,
        )
    }
}
