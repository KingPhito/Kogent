package com.ralphdugue.kogent.dataconnector.adapters.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingModel

class HuggingFaceEmbeddingModel(
    private val config: EmbeddingConfig.HuggingFaceEmbeddingConfig,
    private val apiDataConnector: APIDataConnector,
) : EmbeddingModel {
    override suspend fun getEmbedding(text: String): FloatArray {
        TODO("Not yet implemented")
    }
}
