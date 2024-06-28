package com.ralphdugue.kogent.dataconnector.adapters.connectors

import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentEmbeddingModel
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIDataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIResponse
import com.ralphdugue.kogent.indexing.domain.entities.KogentDocument
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single(binds = [KogentAPIDataConnector::class])
class KogentAPIDataConnectorImpl(
    private val client: HttpClient,
    private val embeddingModel: KogentEmbeddingModel,
) : KogentAPIDataConnector {
    override suspend fun createDocument(
        data: KogentAPIResponse<String>,
        source: KogentDataSource,
    ): KogentDocument {
        TODO("Not yet implemented")
    }

    override suspend fun fetchData(dataSource: KogentDataSource): KogentAPIResponse<String> {
        TODO("Not yet implemented")
    }

    override suspend fun postData(
        dataSource: KogentDataSource,
        data: String,
    ): KogentAPIResponse<String> {
        TODO("Not yet implemented")
    }

    private suspend fun getResponse(source: KogentAPIDataSource): KogentAPIResponse<String> =
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
