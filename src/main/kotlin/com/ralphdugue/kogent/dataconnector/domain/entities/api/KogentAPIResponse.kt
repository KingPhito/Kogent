package com.ralphdugue.kogent.dataconnector.domain.entities.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

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
