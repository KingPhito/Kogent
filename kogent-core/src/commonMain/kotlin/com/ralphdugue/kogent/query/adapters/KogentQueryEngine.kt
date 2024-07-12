package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.api.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSource
import com.ralphdugue.kogent.query.domain.entities.LLMResponse
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.Operation
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import com.ralphdugue.kogent.query.utils.QueryUtils
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [QueryEngine::class])
class KogentQueryEngine(
    private val retriever: Retriever,
    private val llModel: LLModel,
    private val apiDataConnector: APIDataConnector,
    private val sqlDataConnector: SQLDataConnector,
) : QueryEngine {
    override suspend fun processQuery(query: String): String {
        val context = retriever.retrieve(query)
        val prompt = generatePrompt(query, context)
        val response = llModel.query(prompt)
        val llmResponse = parseLLMResponse(response)
        when (llmResponse.operation) {
            is Operation.ApiCall -> TODO()
            is Operation.SqlQuery -> TODO()
            null -> {}
        }
        return llmResponse.answer
    }

    override suspend fun addDataSource(dataSource: DataSource): Boolean {
        return try {
            when (dataSource) {
                is APIDataSource -> apiDataConnector.indexData(dataSource)
                is SQLDataSource -> sqlDataConnector.indexData(dataSource)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeDataSource(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateDataSource(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    private fun parseLLMResponse(response: String): LLMResponse {
        return try {
            QueryUtils.json.decodeFromString<LLMResponse>(response)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid response format")
        }
    }

    private fun generatePrompt(query: String, context: String): String =
        """
            You are a helpful AI assistant that can process natural language queries related to various data sources. 
            Analyze the query and the context provided below. Based on your analysis, provide a response 
            that adheres strictly to the following JSON schema:

            ${QueryUtils.responseSchema}

            Here are some examples of valid responses:
            ${QueryUtils.answerOnly}
            ${QueryUtils.sqlUpdate}
            ${QueryUtils.apiCall}
            
            Query: $query
            Context: $context
        """.trimIndent()


}
