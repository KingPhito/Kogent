package com.ralphdugue.kogent.query.adapters

import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig

class HuggingFaceLLModel(
    private val config: LLModelConfig.HuggingFaceLLModelConfig,
    private val apiDataConnector: APIDataConnector,
) : LLModel {
    override suspend fun query(text: String): String {
        TODO("Not yet implemented")
    }
}
