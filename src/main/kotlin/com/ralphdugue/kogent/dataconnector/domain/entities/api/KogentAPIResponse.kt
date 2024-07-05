package com.ralphdugue.kogent.dataconnector.domain.entities.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.buildJsonObject

sealed interface KogentAPIResponse<out T : Any> {
    data class Success<out T : Any>(
        val data: T?,
    ) : KogentAPIResponse<T>

    data class Error(
        val exception: Throwable,
    ) : KogentAPIResponse<Nothing>
}

suspend fun getResponse(
    client: HttpClient,
    source: APIDataSource,
): KogentAPIResponse<String> =
    try {
        val response =
            when (source.method) {
                APIDataSource.HttpMethod.GET -> {
                    client.get(source.url + source.endpoint) {
                        source.headers?.forEach { header(it.key, it.value) }
                        source.queryParams?.forEach { parameter(it.key, it.value) }
                    }
                }
                APIDataSource.HttpMethod.POST -> {
                    client.post(source.url + source.endpoint) {
                        source.headers?.forEach { header(it.key, it.value) }
                        source.queryParams?.forEach { parameter(it.key, it.value) }
                        source.body?.let { setBody(buildJsonObject { }) }
                    }
                }
                APIDataSource.HttpMethod.PUT -> TODO()
                APIDataSource.HttpMethod.DELETE -> TODO()
            }
        KogentAPIResponse.Success(response.body())
    } catch (e: Exception) {
        KogentAPIResponse.Error(e)
    }
