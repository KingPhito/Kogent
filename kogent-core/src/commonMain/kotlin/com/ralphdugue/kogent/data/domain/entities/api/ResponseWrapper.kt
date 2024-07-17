package com.ralphdugue.kogent.data.domain.entities.api

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

suspend fun getResponse(
    client: HttpClient,
    source: APIDataSource,
): Result<String> =
    runCatching {
        val json = Json { ignoreUnknownKeys = true }
        val response =
            when (source.method) {
                APIDataSource.HttpMethod.GET -> {
                    client.get(source.baseUrl + source.endpoint) {
                        source.headers?.forEach { header(it.key, it.value) }
                        source.queryParams?.forEach { parameter(it.key, it.value) }
                    }
                }
                APIDataSource.HttpMethod.POST -> {
                    client.post(source.baseUrl + source.endpoint) {
                        contentType(ContentType.Application.Json)
                        source.headers?.forEach { header(it.key, it.value) }
                        source.queryParams?.forEach { parameter(it.key, it.value) }
                        source.body?.let { setBody(json.encodeToJsonElement(it)) }
                    }
                }
                APIDataSource.HttpMethod.PUT -> TODO()
                APIDataSource.HttpMethod.DELETE -> TODO()
            }
        when (response.status.value) {
            in 200..299 -> response.bodyAsText()
            else -> error("Failed to fetch data from source")
        }
    }

