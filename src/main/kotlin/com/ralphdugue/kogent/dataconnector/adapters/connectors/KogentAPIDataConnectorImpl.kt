package com.ralphdugue.kogent.dataconnector.adapters.connectors

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIResponse
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Document
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single(binds = [APIDataConnector::class])
class KogentAPIDataConnectorImpl(
    private val client: HttpClient,
    private val embeddingModel: EmbeddingModel,
) : APIDataConnector {
    override suspend fun indexData(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fetchData(dataSource: DataSource): KogentAPIResponse<String> {
        TODO("Not yet implemented")
    }

    override suspend fun postData(
        dataSource: DataSource,
        data: String,
    ): KogentAPIResponse<String> {
        TODO("Not yet implemented")
    }

    private suspend fun createDocument(
        data: KogentAPIResponse<String>,
        source: DataSource,
    ): Document {
        TODO("Not yet implemented")
    }

    private suspend fun getResponse(source: APIDataSource): KogentAPIResponse<String> =
        try {
            val response =
                client.get(source.url + source.endpoint) {
                    source.headers?.forEach { (key, value) ->
                        header(key, value)
                    }
                    source.queryParams?.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
            KogentAPIResponse.Success(response.body())
        } catch (e: Exception) {
            KogentAPIResponse.Error(e)
        }
}
