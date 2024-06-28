package com.ralphdugue.kogent.dataconnector.adapters.embedding

import com.ralphdugue.kogent.dataconnector.domain.entities.KogentEmbeddingModel
import com.ralphdugue.kogent.dataconnector.domain.entities.api.KogentAPIDataConnector

class HuggingFaceEmbeddingModel(
    private val dataConnector: KogentAPIDataConnector,
) : KogentEmbeddingModel {
    override suspend fun getEmbedding(text: String): FloatArray {
        TODO("Not yet implemented")
    }
}
