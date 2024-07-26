package com.ralphdugue.kogent.query.adapters

import co.touchlab.kermit.Logger
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.query.domain.entities.LLMResponse
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.Operation
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import com.ralphdugue.kogent.query.utils.PromptUtils
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import com.ralphdugue.kogent.retrieval.utils.RetrieverUtils
import org.koin.core.annotation.Single

@Single(binds = [QueryEngine::class])
class KogentQueryEngine(
    private val retriever: Retriever,
    private val llModel: LLModel,
    private val dataSourceRegistry: DataSourceRegistry,
    private val apiDataConnector: APIDataConnector,
    private val sqlDataConnector: SQLDataConnector,
) : QueryEngine {
    override suspend fun processQuery(query: String): String {
        val retrievedResult = retriever.retrieve(query)
        val documents = retrievedResult.getOrElse {
            Logger.e(it) { "Failed to retrieve documents: ${it.localizedMessage}" }
            return "There was an error processing the request: ${it.localizedMessage}"
       }
        val prompt = generatePrompt(query, RetrieverUtils.buildContext(query, documents))
        val response = llModel.query(prompt)
        val llmResponse = parseLLMResponse(response)
        return if (llmResponse.needsUpdate) {
            val result = dataSourceRegistry.getDataSourceById(llmResponse.operation!!.dataSourceId)
            result.fold(
                onSuccess = { dataSource ->
                    val operationResult = when (llmResponse.operation) {
                        is Operation.ApiCall -> {
                            apiDataConnector.postData(dataSource as APIDataSource, llmResponse.operation.body)
                        }
                        is Operation.SqlQuery -> {
                            sqlDataConnector.writeQuery(dataSource as SQLDataSource, llmResponse.operation.query)
                        }
                    }
                    operationResult.fold(
                        onSuccess = { llmResponse.answer + "\nData source updated successfully." },
                        onFailure = {
                            Logger.e(it) { "Failed to update data source: ${it.localizedMessage}" }
                            "There was an error processing the request: ${it.localizedMessage}"
                        }
                    )
                },
                onFailure = {
                    Logger.e(it) { "Failed to update data source: ${it.localizedMessage}" }
                    "There was an error processing the request: ${it.localizedMessage}"
                }
            )
        } else llmResponse.answer
    }

    override suspend fun addDataSource(dataSource: DataSource) {
        when (dataSource) {
            is APIDataSource -> {
                apiDataConnector.indexData(dataSource).fold(
                    onSuccess = { Logger.i { "Data source ${dataSource.identifier} added successfully" } },
                    onFailure = { Logger.e(it) { "Failed to add data source ${dataSource.identifier}" } }
                )
            }
            is SQLDataSource -> {
                sqlDataConnector.indexData(dataSource).fold(
                    onSuccess = { Logger.i { "Data source ${dataSource.identifier} added successfully" } },
                    onFailure = { Logger.e(it) { "Failed to add data source ${dataSource.identifier}" } }
                )
            }
        }
    }

    override suspend fun removeDataSource(dataSource: DataSource) {
        when (dataSource) {
            is APIDataSource -> {
                apiDataConnector.removeData(dataSource).fold(
                    onSuccess = { Logger.i { "Data source ${dataSource.identifier} removed successfully" } },
                    onFailure = { Logger.e(it) { "Failed to remove data source ${dataSource.identifier}" } }
                )
            }
            is SQLDataSource -> {
                sqlDataConnector.removeData(dataSource).fold(
                    onSuccess = { Logger.i { "Data source ${dataSource.identifier} removed successfully" } },
                    onFailure = { Logger.e(it) { "Failed to remove data source ${dataSource.identifier}" } }
                )
            }
        }
    }

    override suspend fun updateDataSource(dataSource: DataSource) {
        when (dataSource) {
            is APIDataSource -> {
                apiDataConnector.updateData(dataSource).fold(
                    onSuccess = { Logger.i { "Data source ${dataSource.identifier} updated successfully" } },
                    onFailure = { Logger.e(it) { "Failed to update data source ${dataSource.identifier}" } }
                )
            }
            is SQLDataSource -> {
                sqlDataConnector.updateData(dataSource).fold(
                    onSuccess = { Logger.i { "Data source ${dataSource.identifier} updated successfully" } },
                    onFailure = { Logger.e(it) { "Failed to update data source ${dataSource.identifier}" } }
                )
            }
        }
    }

    private fun parseLLMResponse(response: String): LLMResponse {
        return try {
            PromptUtils.json.decodeFromString<LLMResponse>(response)
        } catch (e: Exception) {
            LLMResponse(answer = response, operation = null, needsUpdate = false)
        }
    }

    private fun generatePrompt(query: String, context: String): String =
        """
            You are a helpful AI assistant that can process natural language requests from users related 
            to various data sources. Analyze the query and the context provided below. Based on your analysis, 
            provide a response that adheres strictly to the following JSON schema:

            ${PromptUtils.responseSchema}

            Here are some examples of valid responses:
            ${PromptUtils.answerOnly}
            ${PromptUtils.sqlUpdate}
            ${PromptUtils.apiCall}
            
            User request: $query
            Context: $context
        """.trimIndent()


}
