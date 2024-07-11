package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import org.koin.core.annotation.Single

@Single(binds = [QueryEngine::class])
class KogentQueryEngine(
    private val retriever: Retriever,
    private val llModel: LLModel,
    private val apiDataConnector: APIDataConnector,
    private val sqlDataConnector: SQLDataConnector,
) : QueryEngine {
    override suspend fun sendUserQuery(query: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun addDataSource(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeDataSource(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateDataSource(dataSource: DataSource): Boolean {
        TODO("Not yet implemented")
    }
}
