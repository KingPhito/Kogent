package com.ralphdugue.kogent.data.adapters.connectors

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Document
import io.ktor.client.*
import org.koin.core.annotation.Single

@Single(binds = [APIDataConnector::class])
class KogentAPIDataConnector(
    private val client: HttpClient,
    private val embeddingModel: EmbeddingModel,
    private val dataSourceRegistry: DataSourceRegistry,
) : APIDataConnector {
    override suspend fun indexData(dataSource: DataSource): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateData(dataSource: DataSource): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeData(dataSource: DataSource): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchData(dataSource: APIDataSource): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun postData(
        dataSource: APIDataSource,
        data: String?
    ): Result<String> {
        TODO("Not yet implemented")
    }

    private fun createDocument(
        data: Result<String>,
        source: DataSource,
    ): Document {
        TODO("Not yet implemented")
    }
}
